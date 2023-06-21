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
package cn.dev33.satoken.jfinal;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.redis.serializer.ISerializer;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import redis.clients.jedis.util.SafeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaJdkSerializer implements ISerializer {

    public static final ISerializer me = new JdkSerializer();

    public byte[] keyToBytes(String key) {
        return SafeEncoder.encode(key);
    }

    public String keyFromBytes(byte[] bytes) {
        return SafeEncoder.encode(bytes);
    }

    public byte[] fieldToBytes(Object field) {
        return valueToBytes(field);
    }

    public Object fieldFromBytes(byte[] bytes) {
        return valueFromBytes(bytes);
    }

    public byte[] valueToBytes(Object value) {
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
                    LogKit.error(e.getMessage(), e);}
        }
    }

    public Object valueFromBytes(byte[] bytes) {
        if(bytes == null || bytes.length == 0)
            return null;

        ObjectInputStream objectInput = null;
        try {
            ByteArrayInputStream bytesInput = new ByteArrayInputStream(bytes);
            objectInput = new ObjectInputStream(bytesInput);
            return objectInput.readObject();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (objectInput != null)
                try {objectInput.close();} catch (Exception e) {LogKit.error(e.getMessage(), e);}
        }
    }
}
