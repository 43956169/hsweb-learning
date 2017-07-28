import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearch {

    /* 
     * 创建索引 
     */
    private static void CreateIndex(Client client){
        String json = "{" +
                "\"user\":\"daiyutage\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";

        IndexResponse response = client.prepareIndex("twitter", "tweet","2")
                .setSource(json)
                .get();
        // Index name
        String _index = response.getIndex();
        System.out.println("index:"+_index);
        // Type name  
        String _type = response.getType();
        System.out.println("_type:"+_type);
        // Document ID (generated or not)  
        String _id = response.getId();
        // Version (if it's the first time you index this document, you will get: 1)  
        long _version = response.getVersion();
        System.out.println("_version:"+_version);
        // isCreated() is true if the document is a new one, false if it has been updated  
        boolean created = response.isCreated();

    }
    /* 
     * 根据索引获取信息 
     */
    public static void GetResponse(Client client){

        GetResponse reponse = client.prepareGet("twitter","tweet","2").get();

        String _source = reponse.getSourceAsString();

        System.out.println(_source);

    }
    /* 
     * 删除记录
     */
    public static void DeleteResponse(Client client){
        DeleteResponse reponse = client.prepareDelete("twitter","tweet","2").get();

        System.out.println(reponse.isFound());

    }

    /**
     * 删除索引
     */
    private static boolean deleteIndex(String indexName) {
        try {
            Client client = EsClient.getClient();

            IndicesExistsResponse indicesExistsResponse = client.admin()
                    .indices().prepareExists(indexName).execute().actionGet();
            if (indicesExistsResponse.isExists()) {
                //System.out.println("Exists");
                IndicesAdminClient admin = client.admin().indices();
                return admin.prepareDelete(indexName).execute().actionGet()
                        .isAcknowledged();
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* 
     * update 
     */
    private static void UpdateRequest(Client client) throws IOException, InterruptedException, ExecutionException {

        //当更新的内容不存在的时候会添加  
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("twitter");
        updateRequest.type("tweet");
        updateRequest.id("3");
        XContentBuilder jsonBuilder =XContentFactory.jsonBuilder();
        updateRequest.doc(jsonBuilder
                .startObject()
                .field("message","我是林志颖啊")
                .field("user","lin")
                .endObject());
        UpdateResponse response= client.update(updateRequest).get();  
           
    /*   
     *  //当更新的内容不存在时候，不会添加 
        UpdateResponse response = client.prepareUpdate("twitter","tweet","1") 
                        .setDoc(jsonBuilder() 
                                 .startObject() 
                                   .field("users4","xue") 
                                 .endObject()) 
                                 .get();*/

        System.out.println(response.getVersion());


    }
    /* 
     * 多行查询 
     */
    private static void MultiGet(Client client) {

        MultiGetResponse response = client.prepareMultiGet()
                .add("twitter","tweet","2","1")
                .get();
        for(MultiGetItemResponse itemResponse:response){
            GetResponse getResponse = itemResponse.getResponse();
            if(getResponse.isExists()){
                System.out.println(getResponse.getSourceAsString());
            }
        }
    }
    /* 
     * BulkIndex 
     */
    private static void BulkIndex(Client client) throws IOException {

        BulkRequestBuilder requestBuilder = client.prepareBulk();

        requestBuilder.add(client.prepareIndex("twitter","tweet","4")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user","niekaijie")
                        .field("school","beiyou")
                        .field("address","haidianqu")
                        .endObject())
        );
        requestBuilder.add(client.prepareIndex("twitter","tweet","3")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user","林志颖aa")
                        .field("school","台湾大学")
                        .field("address","台湾")
                        .endObject())
        );
        BulkResponse response = requestBuilder.get();


    }

    /* 
     * search 
     */
    private static void SearchIndex(Client client) {

        SearchResponse response = client.prepareSearch("twitter")
                .setTypes("tweet")
                .setQuery(QueryBuilders.termQuery("user", "lin"))

                .execute()
                .actionGet();
        SearchHits hits = response.getHits();

        for(SearchHit hit: hits.getHits()){

            System.out.println(hit.getSource().get("message"));


        }

    }


    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {


        Client client = EsClient.getClient();

//      CreateIndex(client);

//      GetResponse(client);

//      DeleteResponse(client);

//      UpdateRequest(client);
        client.close();

//      MultiGet(client);

//      BulkIndex(client);

//        SearchIndex(client);


    }


}  