package com.wanzi.wechatrecord

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        for(i in 1..4){
            println(i)
        }

        var a= arrayOf(1,6,10)
        for(i in a){
            println(i)
        }

        var s="tfdgdghdg"
        for(i in s){
            println(i)
        }
        println("$s 的长度是 ${s.length}")

        assertEquals(4, 2 + 2)
    }

    @Test
    fun testCondition(){
        var a = 10;
        var b = 20;
        var max = if(a>b) a else b;
        println(max);

        val items = listOf("apple", "banana", "kiwi")
        for(item in items){
            if(item is String){
                println(item)
            }
        }
         for(i in items.indices){
             println("index $i value is ${items[i]}")
         }

        var person: Person =  Person();
        person.name  = "aaa"
        person.age = 10
        person.eat("苹果")

        println("${person.name} : ${person.age}")



    }

    class Person : IPerson{


        var name: String = ""
            get() = field
            
        var age: Int = 18
            set(value) { field=value*2  }

        override fun eat(food:String){
                println("$name eat $food")
        }
    }


    interface IPerson {
        fun eat(food: String){
            println("eat $food")
        }
    }

           
}
