import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.ws.http.HTTPException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

public class TweekApiClient implements TweekApiClientInterface
{
    private final String JSON_MEDIATYPE = "application/json";

    HttpClient client;
    private String url;

    public TweekApiClient(String baseUrl) {
    	initialize(baseUrl, null);
    }
    
    public TweekApiClient(String baseUrl, String apiClientName) {
    	initialize(baseUrl, apiClientName);
    }
    
    private void initialize(String baseUrl, String apiClientName) {
    	this.url = String.format("%s/api/v1", baseUrl);
    	
    	if (apiClientName == null) {
    		client = HttpClientBuilder.create().build();
    	} else {
    		Collection<Header> defaultHeaders = new ArrayList<>();
    		defaultHeaders.add(new BasicHeader("X-Api-Client", apiClientName));
    		client = HttpClientBuilder
    				.create()
    				.setDefaultHeaders(defaultHeaders)
    				.build();
    	}
    }
    
    @Override
    public Boolean getBoolean(String keyPath) throws Exception {
    	return getBoolean(keyPath, null, null);
    }
    
    @Override
    public Boolean getBoolean(String keyPath, Map<String, String> context) throws Exception {
    	return getBoolean(keyPath, context, null);
    }
    
    @Override
    public Boolean getBoolean(String keyPath, Map<String, String> context, GetRequestOptions options) throws Exception {
    	return get(keyPath, context, options, new TypeToken<Boolean>(){});
    }
    
    
    @Override
	public String getString(String keyPath) throws Exception {
    	return getString(keyPath,null,null);
    }
    
    @Override
	public String getString(String keyPath, Map<String, String> context) throws Exception {
		
		return getString(keyPath, context, null);
	}
    
    @Override
    public String getString(String keyPath, Map<String, String> context, GetRequestOptions options) throws Exception {
    	
    	return get(keyPath, context, options, new TypeToken<String>(){});
    }
    
    
    
    @Override
    public Integer getNumber(String keyPath) throws Exception {
    	return getNumber(keyPath,null,null);
    }
    
    @Override
	public Integer getNumber(String keyPath, Map<String, String> context) throws Exception {
		
		return getNumber(keyPath, context, null);
	}
    
    @Override
    public Integer getNumber(String keyPath, Map<String, String> context, GetRequestOptions options) throws Exception {
    	
    	return get(keyPath, context, options, new TypeToken<Integer>(){});
    }
    
    
    @Override
    public <T> T get(String keyPath, TypeToken<T> typeToken) throws Exception {
    	return get(keyPath, null, null, typeToken);
    }
    
    @Override
	public <T> T get(String keyPath, Map<String, String> context, TypeToken<T> typeToken) throws Exception {
		
		return get(keyPath, context, null, typeToken);
	}
    
    @Override
    public <T> T get(String keyPath, Map<String, String> context, GetRequestOptions options, TypeToken<T> typeToken) throws Exception
    {
        List<Entry<String,String>> parameters = null;
        
        if (context == null) {
        	parameters = new ArrayList<Entry<String,String>>(); 
        } else {
        	parameters = context.entrySet().stream().collect(Collectors.toList());
        }
        
        if (options != null) {
        	if(options.isFlatten()) {
            	parameters.add(new EntryImpl<String, String>("$flatten", "true"));
            }
        	
        	if(options.shouldIgnoreKeyTypes()) {
        		parameters.add(new EntryImpl<String, String>("$ignoreKeyTypes", "true"));
        	}
        	
        	if(options.getIncludes() != null) {
        		for(String item : options.getIncludes())
                {
                    parameters.add(new EntryImpl<String, String>("$include", item));
                }
        	}
        }

        StringBuilder queryString = new StringBuilder();
        
        if (!parameters.isEmpty()) {
        	parameters.forEach(pair -> queryString.append(String.format("%s=%s&", pair.getKey(), pair.getValue())));
        	queryString.deleteCharAt(queryString.length() - 1);
        }
        
        T keyResponse = requestKey(String.format("%s?%s", keyPath, queryString.toString()), typeToken.getType());

        return keyResponse;
    }
    
    @Override
	public void appendContext(String identityType, String identityId, Map<String, Object> context)
			throws ClientProtocolException, IOException {
    	String url = String.format("%s/context/%s/%s", this.url, identityType, identityId);
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-type", JSON_MEDIATYPE);
		Gson gson = new Gson();
		StringEntity json = new StringEntity(gson.toJson(context));
		post.setEntity(json);

		HttpResponse response = client.execute(post);
		
		Integer statusCode = response.getStatusLine().getStatusCode();
		
		if (statusCode >= 300) {
			throw new HTTPException(statusCode);
		}
	}
    
    @Override
	public void deleteContextProperty(String identityType, String identityId, String property) throws ClientProtocolException, IOException {
    	String url = String.format("%s/context/%s/%s/%s", this.url, identityType, identityId, property);
		HttpDelete delete = new HttpDelete(url);
		HttpResponse response = client.execute(delete);
		
		Integer statusCode = response.getStatusLine().getStatusCode();

		if (statusCode >= 300) {
			System.out.println(statusCode);
			throw new HTTPException(statusCode);
		}
	}
    
    private <T> T requestKey(String keyPath, Type type) throws Exception {

		String fullUrl = String.format("%s/keys/%s", this.url, keyPath);
    	HttpGet request = new HttpGet(fullUrl);

    	HttpResponse response = client.execute(request);
    	
    	BufferedReader rd = new BufferedReader(
        		new InputStreamReader(response.getEntity().getContent()));

    	StringBuffer result = new StringBuffer();
    	String line = "";
    	while ((line = rd.readLine()) != null) {
    		result.append(line);
    	}
        	
		Gson gson = new Gson();
		
		return gson.fromJson(result.toString(), type);
	}
    
    final class EntryImpl<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public EntryImpl(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
