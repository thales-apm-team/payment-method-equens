package torenameEquens.utils.http;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static torenameEquens.utils.http.HttpTestUtils.mockHttpResponse;

class StringResponseTest {

    @Test
    void fromHttpResponse_nominal(){
        // given: a complete HTTP response
        CloseableHttpResponse httpResponse = mockHttpResponse( 200, "OK", "some content",
                new Header[]{ new BasicHeader("Name", "Value")} );

        // when: converting it to StringResponse
        StringResponse stringResponse = StringResponse.fromHttpResponse( httpResponse );

        // then: the StringResponse attributes match the content of the HttpResponse
        assertNotNull( stringResponse );
        assertEquals( 200, stringResponse.getStatusCode() );
        assertEquals( "OK", stringResponse.getStatusMessage() );
        assertEquals( "some content", stringResponse.getContent() );
        assertEquals( 1, stringResponse.getHeaders().size() );
    }

    @Test
    void fromHttpResponse_null(){
        // when: converting null to StringResponse
        StringResponse stringResponse = StringResponse.fromHttpResponse( null );

        // then: the StringResponse is null
        assertNull( stringResponse );
    }

}
