/*
 * Copyright 2018 Google LLC
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
package hello.common;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.dlp.v2.DlpServiceClient;

public class DLPServiceFactory {
	public static final Logger LOG = LoggerFactory.getLogger(DLPServiceFactory.class);
	private static DlpServiceClient instance = null;

	public static synchronized DlpServiceClient getService() throws IOException, GeneralSecurityException {
		if (instance == null) {
			instance = buildService();
		}
		return instance;
	}

	private static DlpServiceClient buildService() throws IOException, GeneralSecurityException {

		return DlpServiceClient.create();
	}

}
