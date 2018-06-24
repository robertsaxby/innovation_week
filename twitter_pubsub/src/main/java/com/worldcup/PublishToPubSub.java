package com.worldcup;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;

public class PublishToPubSub implements AutoCloseable{

    private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
    private final ProjectTopicName TOPIC_NAME;
    private final Publisher publisher;

    PublishToPubSub(String topic) throws Exception{
        TOPIC_NAME = ProjectTopicName.of(PROJECT_ID, topic);

        // Build publisher, default settings
        publisher = Publisher.newBuilder(TOPIC_NAME).build();
    }

    @Override
    public void close() throws Exception{
        if (publisher != null) {
            publisher.shutdown();
        }
    }

    void put(String message) {
        ByteString data = ByteString.copyFromUtf8(message);

        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
            .setData(data)
            .build();

        //schedule a message to be published, messages are automatically batched
        ApiFuture<String> future = publisher.publish(pubsubMessage);

        // add an asynchronous callback to handle success or failure
        ApiFutures.addCallback(future, new ApiFutureCallback<String>() {

            @Override
            public void onFailure(Throwable throwable) {
                if (throwable instanceof ApiException) {
                    ApiException apiException = ((ApiException) throwable);

                    System.out.println(apiException.getStatusCode().getCode());
                    System.out.println(apiException.isRetryable());
                }
                System.out.println("Error publishing message : " + message);
            }

            @Override
            public void onSuccess(String messageId) {
                // Returns server-assigned message ids (unique within the topic)
                System.out.println(messageId);
            }
        });
    }
}
