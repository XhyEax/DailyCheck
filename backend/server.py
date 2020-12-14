from flask import Flask
from flask import request
from datetime import datetime, date
import redis
import time

# 表名
USERTABLE = "usertable"
CHECKSET = "checkset"
# 连接redis
redis_conn = redis.StrictRedis(host='localhost', port=6379, db=0)
# 初始化flask
app = Flask(__name__)

def getDayTime():
    day_time = int(time.mktime(date.today().timetuple()))
    return str(day_time)


def getCheckSet():
    strDayTime = getDayTime()
    intDayTime = int(strDayTime)
    # 第二天，清空不属于今天的数据
    if(redis_conn.get('lastDayTime') == None or int(redis_conn.get('lastDayTime')) < intDayTime):
        redis_conn.set('lastDayTime', strDayTime)
        redis_conn.zremrangebyscore(CHECKSET, 0, intDayTime-1)
    return redis_conn.zrange(CHECKSET, 0, -1, withscores=True)

def getCheckList():
    newCheckList = []
    for (uid, timestamp) in getCheckSet():
        name = uid2name(uid)
        time = datetime.fromtimestamp(int(timestamp)).strftime('%H:%M')
        item = {"name": name, "time": time}
        newCheckList.append(item)
    return newCheckList

# 获取签到名次
def getCheckNo(uid):
    no = redis_conn.zrank(CHECKSET, uid)
    if(no == None):
        no = -2
    return no + 1

# 签到
def checkByUid(uid):
    redis_conn.zadd(CHECKSET, {uid: int(time.time())})

def uid2name(uid):
    name = redis_conn.hget(USERTABLE, int(uid))
    if(name):
        name = name.decode(encoding='UTF-8')
    return name

def addUidByName(name):
    usertable = redis_conn.hgetall(USERTABLE)
    uid = len(usertable)
    redis_conn.hset(USERTABLE, str(uid), str(name))
    return str(uid)

def checkName(name):
    return len(name) > 0 and (" " not in name) and len(name) <= 20

@app.route('/register', methods=['GET'])
def register():
    name = request.args.get("name")
    if(checkName(name)):
        uid = addUidByName(name)
        ret = {"success": True, "msg": "注册成功", "uid": uid}
    else:
        ret = {"success": False, "msg": "格式错误", "uid": "-1"}
    return (ret)

@app.route('/getCheckState', methods=['GET'])
def getCheckState():
    uid = str(request.args.get("uid"))
    if(uid and uid2name(uid)):
        isChecked = (getCheckNo(uid) != -1)
        ret = {"success": True, "msg": "查询成功", "isChecked": isChecked}
    else:
        ret = {"success": False, "msg": "查询失败", "isChecked": False}
    return (ret)

@app.route('/check', methods=['GET'])
def check():
    uid = str(request.args.get("uid"))
    if(uid and uid2name(uid)):
        isChecked = (getCheckNo(uid) != -1)
        if(isChecked):
            ret = {"success": False, "msg": "你今天已经签过到了", "isChecked": True}
        else:
            checkByUid(uid)
            no = str(getCheckNo(uid))
            ret = {"success": True, "msg": "签到成功，你是今天第"+no+"个签到的", "isChecked": True}
    else:
        ret = {"success": False, "msg": "签到失败，uid不能为空", "isChecked": False}
    return (ret)

@app.route('/getCheckList', methods=['GET'])
def doGetCheckList():
    ret = {"success": True, "data": getCheckList()}
    return (ret)

if __name__ == '__main__':
    app.run(debug=False,port=5001)