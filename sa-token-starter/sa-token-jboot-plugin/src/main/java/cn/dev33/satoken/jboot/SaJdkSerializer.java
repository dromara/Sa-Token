/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.jboot;

import com.jfinal.log.Log;
import io.jboot.components.serializer.JbootSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaJdkSerializer implements JbootSerializer {

    private static final Log LOG = Log.getLog(SaJdkSerializer.class);

    @Override
    public byte[] serialize(Object value) {
        if (value == null) {
            return null;
        }
        ObjectOutputStream objectOut = null;
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(1024);
            objectOut = new ObjectOutputStream(bytesOut);
            objectOut.writeObject(value);
            objectOut.flush();
            return bytesOut.toByteArray();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(objectOut != null)
                try {objectOut.close();} catch (Exception e) {
                    LOG.error(e.getMessage(), e);}
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            // Configure mapper for safe deserialization
            mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, true);
            mapper.configure(MapperFeature.AUTO_DETECT_SETTERS, true);
            mapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            
            // Use mapper instead of ObjectInputStream
            return mapper.readValue(bais, _clazz);
        } catch (Exception e) {
            LOG.error("Error during deserialization of bytes", e);
            throw new RuntimeException(e);
        } finally {
            try {
                bais.close();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
