package obfuscate.service;

// https://github.com/googleapis/google-cloud-java/blob/master/google-cloud-examples/src/main/java/com/google/cloud/examples/storage/snippets/StorageSnippets.java

/*
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * EDITING INSTRUCTIONS
 * This file is referenced in Storage's javadoc. Any change to this file should be reflected in
 * Storage's javadoc.
 */

// package com.google.cloud.examples.storage.snippets;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

public class GCSProvider {

    private final Storage storage;

    public GCSProvider(Storage storage) {
        this.storage = storage;
    }

    public WriteChannel getWriter(String bucketName, String blobName) {
        BlobId blobId = BlobId.of(bucketName, blobName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        return storage.writer(blobInfo);
    }
}