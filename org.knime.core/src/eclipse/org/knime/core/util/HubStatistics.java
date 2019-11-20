/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Nov 20, 2019 (Moritz Heine, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;

import org.knime.core.node.KNIMEConstants;

/**
 * Class to write/read some KNIME Hub statistics into/from the workspace.
 *
 * @author Moritz Heine, KNIME GmbH, Konstanz, Germany
 *
 * @noreference This class is not intended to be referenced by clients.
 *
 * @since 4.1
 */
public class HubStatistics {

    /**
     * Name for the KNIME Hub json used to store values such as {@link #LAST_KNIME_HUB_LOGIN} and
     * {@link #LAST_KNIME_HUB_UPLOAD}.
     */
    private static String KNIME_HUB_USAGE_FILE = "knimehub_usage.json";

    /**
     * Key for shared Memento to store last login to KNIME Hub.
     */
    public static String LAST_KNIME_HUB_LOGIN = "LastHubLogin";

    /**
     * Key for shared Memento to store last upload to KNIME Hub.
     */
    public static String LAST_KNIME_HUB_UPLOAD = "LastHubUpload";

    private HubStatistics() {
    }

    /**
     * Stores the provided value under the given key into a statistics JSON.
     *
     * @param key The key for the value.
     * @param value The value itself.
     */
    public static synchronized void storeKnimeHubStat(final String key, final String value) {
        try {
            final JsonObject stats = readHubStats(getStatisticsLocation());
            final JsonObjectBuilder builder = Json.createObjectBuilder();

            for (final Entry<String, JsonValue> entry : stats.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }

            builder.add(key, value);

            writeHubStats(builder.build(), getStatisticsLocation());
        } catch (Exception e) {
            // do nothing
        }
    }

    /**
     * Writes the JSON out to disk.
     *
     * @param json The JSON.
     * @param location The file into which the JSON should be written.
     * @throws IOException If an error occurs.
     * @throws FileNotFoundException If an error occurs.
     */
    private static void writeHubStats(final JsonObject json, final File location)
        throws FileNotFoundException, IOException {
        Map<String, Boolean> cfg = Collections.singletonMap(JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
        try (FileOutputStream outStream = new FileOutputStream(location);
                JsonWriter jw = Json.createWriterFactory(cfg).createWriter(outStream)) {
            jw.write(json);
        }
    }

    private static File getStatisticsLocation() {
        return new File(KNIMEConstants.getKNIMEHomeDir(), KNIME_HUB_USAGE_FILE);
    }

    /**
     * Returns the last time the user uploaded to KNIME Hub.
     *
     * @return The last upload to KNIME Hub.
     */
    public static Optional<ZonedDateTime> getLastUpload() {
        try {
            return Optional.ofNullable(ZonedDateTime.parse(getStatistics(LAST_KNIME_HUB_UPLOAD)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Returns the last time the user logged in to KNIME Hub.
     *
     * @return The last logged in to KNIME Hub.
     */
    public static Optional<ZonedDateTime> getLastLogin() {
        try {
            return Optional.ofNullable(ZonedDateTime.parse(getStatistics(LAST_KNIME_HUB_LOGIN)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Gets the statistics for the specified key.
     *
     * @param key The key of the statistics.
     * @return The value, or <code>null</code> if no such value exists.
     */
    private static String getStatistics(final String key) {
        try {
            final JsonObject stats = readHubStats(getStatisticsLocation());

            final JsonValue val = stats.get(LAST_KNIME_HUB_UPLOAD);
            if (val instanceof JsonString) {
                return ((JsonString)val).getString();
            }
        } catch (Exception ex) {
        }

        return null;
    }

    /**
     * Reads the current hub statistics or returns an empty JsonObject in case there aren't any yet.
     *
     * @param location The location holding the statistics.
     * @return The current statistics JSON.
     * @throws FileNotFoundException If file is not found.
     */
    private static JsonObject readHubStats(final File location) throws FileNotFoundException {
        if (!location.exists()) {
            return Json.createObjectBuilder().build();
        }

        try (JsonReader jr = Json.createReader(new FileInputStream(location))) {
            return jr.readObject();
        }
    }
}
