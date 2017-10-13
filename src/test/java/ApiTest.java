import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by ars on 10.10.2017.
 */
public class ApiTest {

    public String urlAuth = "http://liza.maxoptra.com/rest/2/authentication/createSession";
    public String urlOrders = "http://liza.maxoptra.com/rest/2/distribution-api/orders/save";


    public  Response createdSessionID(String accountID, String user,String password){
        Response response = given().
                parameters("accountID", accountID, "user", user, "password", password).
                when().
                post(urlAuth);

        return response;

    }

    public Response saveOrder(String sessionID) {

        Response response = given().accept(ContentType.XML).contentType("charset=utf-8").contentType("application/xml").
                body("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<apiRequest>\n" +
                        "   <sessionID>" + sessionID + "</sessionID>\n" +
                        "   <orders>\n" +
                        "      <order>\n" +
                        "         <orderReference></orderReference>\n" +
                        "         <areaOfControl>BeestonZone</areaOfControl>\n" +
                        "         <date>05/05/2014</date>\n" +
                        "         <client>\n" +
                        "            <name>Customer 1</name>\n" +
                        "            <contactPerson>Gary Davis</contactPerson>\n" +
                        "            <contactNumber>100000001</contactNumber>\n" +
                        "            <contactEmail>jerry@gmail.com</contactEmail>\n" +
                        "         </client>\n" +
                        "         <location>\n" +
                        "            <name>Customer Location 1</name>\n" +
                        "            <address>5 HEDDERWICK HILL,WEST BARNS,DUNBAR,E. LOTHIAN,EH42 1XF</address>\n" +
                        "            <latitude>52.855864</latitude>\n" +
                        "            <longitude>-1.457062</longitude>\n" +
                        "            <globalId>1234567890</globalId>\n" +
                        "            <isVerified>true</isVerified>\n" +
                        "         </location>\n" +

                        "         <dropWindows>\n" +
                        "            <dropWindow>\n" +
                        "               <start>05/05/2014 8:00</start>\n" +
                        "               <end>05/05/2014 18:00</end>\n" +
                        "            </dropWindow>\n" +
                        "         </dropWindows>\n" +
                        "         <priority>3</priority>\n" +
                        "         <durationDrop>00:15</durationDrop>\n" +
                        "         <capacity>100</capacity>\n" +
                        "         <volume>200</volume>\n" +
                        "         <collection>true</collection>\n" +
                        "         <additionalInstructions>some additional instructions</additionalInstructions>\n" +
                        "         <stopSequence>first</stopSequence>\n" +
                        "         <orderItems>\n" +
                        "            <orderItem>\n" +
                        "               <name>Package</name>\n" +
                        "               <barcode>654321684</barcode>\n" +
                        "            </orderItem>\n" +
                        "         </orderItems>\n" +
                        "      </order>\n" +
                        "   </orders>\n" +
                        "</apiRequest>"
                ).
                when().
                post(urlOrders);
        return response;

    }


    @Test
    public void testCreatedOrder() throws Exception {

        Response tmpResponseEuth = createdSessionID("demo","arsen","123456");
        String xmlResponseAuth = tmpResponseEuth.andReturn().asString();
        XmlPath xmlPathAuth = new XmlPath(xmlResponseAuth);

        Assert.assertTrue(tmpResponseEuth.getStatusCode() == 200);

        String sessionID = xmlPathAuth.getString("apiResponse.authResponse.sessionID");

        Response tmpResponseSave = saveOrder(sessionID);
        String xmlResponseSave = tmpResponseSave.thenReturn().asString();
        XmlPath xmlPath = new XmlPath(xmlResponseSave);


        String status = xmlPath.getString("apiResponse.orders.order.status");
        String errorCode = xmlPath.getString("apiResponse.orders.order.errors.error.errorCode");
        String warningCode = xmlPath.getString("apiResponse.orders.order.warnings.warning.warningCode");

        Assert.assertTrue(tmpResponseSave.getStatusCode() == 200);
        Assert.assertEquals(status, "Created");
        Assert.assertEquals(errorCode, "");
        Assert.assertEquals(warningCode, "");


    }
}
