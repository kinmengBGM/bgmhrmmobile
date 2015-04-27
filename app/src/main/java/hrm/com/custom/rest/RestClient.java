package hrm.com.custom.rest;

/**
 * Created by Beans on 4/1/2015.
 */
public class RestClient<T> {
/*    String url;
    RestTemplate restTemplate;

    public RestClient(String url){
        this.url = url;
        this.restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


            final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
            interceptors.add(new BasicAuthInterceptor(username, password));
            restTemplate.setInterceptors(interceptors);

            String[] roleArray = restTemplate.getForObject(url, String[].class, username);

            List<String> result = Arrays.asList(roleArray);
            return result;
    }

    public T get(T myClass, String param) {
       T result = restTemplate.getForObject(url, T.class, param);

        return result;
    }*/
}
