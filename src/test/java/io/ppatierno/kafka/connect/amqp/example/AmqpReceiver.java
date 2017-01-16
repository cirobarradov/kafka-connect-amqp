/*
 * Copyright 2016 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ppatierno.kafka.connect.amqp.example;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import io.vertx.proton.ProtonConnection;
import io.vertx.proton.ProtonReceiver;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.Section;

import java.io.IOException;

public class AmqpReceiver {

    public static void main(String[] args) {

        (new AmqpReceiver()).run();
    }

    public void run() {

        Vertx vertx = Vertx.vertx();

        ProtonClient client = ProtonClient.create(vertx);

        client.connect("localhost", 5673, res -> {

            if (res.succeeded()) {
                System.out.println("Connection successfull");

                ProtonConnection connection = res.result();
                connection.open();

                ProtonReceiver receiver = connection.createReceiver("test");

                receiver.handler((delivery, message) -> {

                    Section body = message.getBody();
                    if (body instanceof Data) {
                        byte[] value = ((Data) body).getValue().getArray();
                        System.out.println(new String(value));
                    }

                })
                .open();
            }
        });


        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
