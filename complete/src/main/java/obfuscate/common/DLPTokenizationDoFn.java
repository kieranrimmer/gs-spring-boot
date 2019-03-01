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
package obfuscate.common;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.dlp.v2.DlpServiceClient;

public class DLPTokenizationDoFn {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8845993157503721016L;
	public static final Logger LOG = LoggerFactory.getLogger(DLPTokenizationDoFn.class);
	private String projectId;
	private DlpServiceClient dlpServiceClient;
	private Optional<String> deIdentifyTemplateName;
	private Optional<String> inspectTemplateName;
	private boolean inspectTemplateExist;

	public DLPTokenizationDoFn(String projectId, Optional<String> deIdentifyTemplateName,
			Optional<String> inspectTemplateName) {
		this.projectId = projectId;
		dlpServiceClient = null;
		this.deIdentifyTemplateName = deIdentifyTemplateName;
		this.inspectTemplateName = inspectTemplateName;
		inspectTemplateExist = false;

	}

	public void startBundle() throws SQLException {

		try {
			this.dlpServiceClient = DlpServiceClient.create();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public void finishBundle() throws Exception {
		if (this.dlpServiceClient != null) {
			this.dlpServiceClient.close();
		}
	}

	/*
	public void processElement(ProcessContext c) {

		String key = c.element().getKey();
		Table nonEncryptedData = c.element().getValue();
		if (this.inspectTemplateName.isAccessible()) {
			if (this.inspectTemplateName.get() != null)
				this.inspectTemplateExist = true;
		}
		ContentItem tableItem = ContentItem.newBuilder().setTable(nonEncryptedData).build();
		DeidentifyContentRequest request;
		DeidentifyContentResponse response;
		if (this.inspectTemplateExist) {
			request = DeidentifyContentRequest.newBuilder().setParent(ProjectName.of(this.projectId).toString())
					.setDeidentifyTemplateName(this.deIdentifyTemplateName.get())
					.setInspectTemplateName(this.inspectTemplateName.get()).setItem(tableItem).build();
		} else {
			request = DeidentifyContentRequest.newBuilder().setParent(ProjectName.of(this.projectId).toString())
					.setDeidentifyTemplateName(this.deIdentifyTemplateName.get()).setItem(tableItem).build();

		}

		response = dlpServiceClient.deidentifyContent(request);
		Table encryptedData = response.getItem().getTable();
		LOG.info("Request Size Successfully Tokenized:{} rows {} bytes ", encryptedData.getRowsList().size(),
				request.toByteString().size());

		List<String> outputHeaders = encryptedData.getHeadersList().stream().map(FieldId::getName)
				.collect(Collectors.toList());
		String[] header = new String[outputHeaders.size()];

		for (int i = 0; i < header.length; i++) {
			header[i] = Util.checkHeaderName(outputHeaders.get(i));

		}
		List<Table.Row> outputRows = encryptedData.getRowsList();

		for (Table.Row outputRow : outputRows) {

			String dlpRow = outputRow.getValuesList().stream().map(value -> value.getStringValue())
					.collect(Collectors.joining(","));
			String[] values = dlpRow.split(",");
			Row row = new Row(key, header, values);
			c.output(row);
		}
	}
	*/
}
