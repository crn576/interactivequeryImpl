# Kafka Stream Interactive Queries

This project is spring boot implementaion of kafka streams interactive queries. Stream threads will consume the data and store it to a local state store. This state store is managed by rocksdb. Every partition thread will have their own set of data. We expose an endpoint where for a given key, we retireve the data from these state stores and give back the response.

This repo can be used for the single instance deployement or multi instance deployement all you need to provide the application host details and rest tempalte to get the data from the managed host.

We also configure the rocksdb, for wriete heavyu workloads , use the appropriate config, inorder to get high performance through put for the streaming data.

It is advised that config has to be tuned as per your infrastructure, such as network, Disk, memory.Higher the messgae payload the higher the risk of overflow errors with respect to the stream threads.


![InstallPhotosBackupAndSync](https://user-images.githubusercontent.com/64941718/115099211-6baa3580-9f02-11eb-90f7-121794a76c2e.jpeg)
