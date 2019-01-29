package com.wanzi.wechatrecord.entry

import org.litepal.crud.DataSupport

/**
 * Created by WZ on 2018-01-29.
 */

class Message : DataSupport() {
    var msgId:Long = 0
    var msgSvrId = ""
    var type = ""
    var talker = ""
    var content = ""
    var isSend = ""           // 1：发送   0：接收   2：系统消息
    var createTime = ""
    var imgPath = ""
}