import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.google.gson.reflect.TypeToken;

public interface TweekApiClientInterface {
	
	Boolean getBoolean(String keyPath) throws Exception;
	Boolean getBoolean(String keyPath, Map<String, String> context) throws Exception;
	Boolean getBoolean(String keyPath, Map<String, String> context, GetRequestOptions options) throws Exception;
	
	String getString(String keyPath) throws Exception;
	String getString(String keyPath, Map<String, String> context) throws Exception;
	String getString(String keyPath, Map<String, String> context, GetRequestOptions options) throws Exception;
	
	Integer getNumber(String keyPath) throws Exception;
	Integer getNumber(String keyPath, Map<String, String> context) throws Exception;
	Integer getNumber(String keyPath, Map<String, String> context, GetRequestOptions options) throws Exception;
	
	<T> T get(String keyPath, TypeToken<T> typeToken) throws Exception;
	<T> T get(String keyPath, Map<String, String> context, TypeToken<T> typeToken) throws Exception;
	<T> T get(String keyPath, Map<String, String> context, GetRequestOptions options, TypeToken<T> typeToken) throws Exception;

    void appendContext(String identityType, String identityId, Map<String,Object> context) throws ClientProtocolException, IOException; 

    void deleteContextProperty(String identityType, String identityId, String property)  throws ClientProtocolException, IOException;
}
