package com.wanzi.wechatrecord

import com.wanzi.wechatrecord.entry.BaseResponse
import com.wanzi.wechatrecord.entry.ChatRoom
import com.wanzi.wechatrecord.entry.Contact
import com.wanzi.wechatrecord.entry.Message

import com.wanzi.wechatrecord.util.RequestManager


class ChatCollectService {

    val baseUrl="http://bi.geebento.com/chat_colect"
    val getMaxMsgIdUrl="/message/getMaxId"
    val saveChatroomUrl="/group/saveChatroom"
    val saveContactUrl="/group/saveContact"
    val saveMesageUrl="/message/save"

    fun getMaxMsgId():Long? {
        var maxId:Long = -1;
        var done:Boolean = false;
        var response : BaseResponse? =  RequestManager().doGet(baseUrl+getMaxMsgIdUrl) ;
        if(response==null){
            return null
        }
        var maxMsgId = response.data;
        if(maxMsgId is Double) return maxMsgId.toLong()
        if(maxMsgId is Long) return maxMsgId
        if(maxMsgId is String) return maxMsgId.toLong()

        return  null
    }

    fun saveChatroom(list:List<ChatRoom>) : Boolean{
        var res:BaseResponse? =  RequestManager().doPostJson(baseUrl+saveChatroomUrl,null,list)
        if(res==null){
            return false;
        }else  if(res.code==200){
            return true;
        }else{
            return false;
        }
    }

    fun saveContact(list:List<Contact>) : Boolean{
        var res:BaseResponse? =  RequestManager().doPostJson(baseUrl+saveContactUrl,null,list)
        if(res==null){
            return false;
        }else  if(res.code==200){
            return true;
        }else{
            return false;
        }
    }

    fun saveMessage(msg: Message):Boolean{
        var res:BaseResponse? =  RequestManager().doPostJson(baseUrl+saveMesageUrl,null,msg)
        if(res==null){
            return false;
        }else  if(res.code==200){
            return true;
        }else{
            return false;
        }
    }
}