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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.bigquery.model.TableSchema;

@SuppressWarnings("serial")
public class BQDestination {

	private static final Logger LOG = LoggerFactory.getLogger(BQDestination.class);

	private Optional<String> datasetName;
	private String projectId;

	public BQDestination(Optional<String> datasetName, String projectId) {
		this.datasetName = datasetName;
		this.projectId=projectId;

	}

	/*

	public Map.Entry<String, List<String>> getDestination(ValueInSingleWindow<Row> element) {

		String key = element.getValue().getTableId();
		String[] headers = element.getValue().getHeader();
		String table_name = String.format("%s:%s.%s",this.projectId, this.datasetName, key);
		LOG.debug("Table Destination {}, {}", table_name, headers.length);
		return Map.Entry.of(table_name, Arrays.asList(headers));
	}

	public TableDestination getTable(Map.Entry<String, List<String>> destination) {

		TableDestination dest = new TableDestination(destination.getKey(), "pii-tokenized output data from dataflow");
		LOG.debug("Table Destination {}", dest.toString());
		return dest;
	}

	*/

	public TableSchema getSchema(Map.Entry<String, List<String>> destination) {

		TableSchema schema = Util.getSchema(destination.getValue());
		LOG.debug("***Schema {}", schema.toString());
		return schema;
	}

}