/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sreejesh.marshal;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MyCamelRouter extends RouteBuilder {




    @Override
    public void configure() throws Exception {
        // start from a timer
        from("timer:myTimer?period=2s&repeatCount=1")
        .routeId("RouteId1")
        // and call the bean
        .setBody(exchange -> new User("John",21, Arrays.asList(
                new Address("India","Bombay","691311"),
                new Address("US","Chicago","11001"),
                new Address("UK","London","LE10SWX")
        )))
        .log("STEP 100: ${body.class.name} - body:${body}")
        .marshal().json(JsonLibrary.Jackson,true)
        .log("STEP 110: ${body.class.name} - body:${body}")
        // and print it to system out via stream component
        .to("file:data/output?fileName=jsonFile.txt")
        ;

        from("file:data/output?fileName=jsonFile.txt&initialDelay=5s")
        .routeId("RouteId2")
        .log("STEP 200: ${body.class.name} - body:${body}")
    //convertBodyTo is not necessary here, added for brevity. Camel will convert to String using TypeConverter
        .convertBodyTo(String.class)
        .log("STEP 210: ${body.class.name} - body:${body}")
        .unmarshal().json(JsonLibrary.Jackson,User.class,true)
//        .unmarshal().json(JsonLibrary.Jackson,Class.forName("com.sreejesh.marshal.User"),true)
        .log("STEP 220: ${body.class.name}")
        .log("STEP 230: ${body}")
        ;


    }

}
