package org.alfresco.rest.framework.tests.core;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.alfresco.rest.framework.resource.content.ContentInfo;
import org.alfresco.rest.framework.resource.content.ContentInfoImpl;
import org.alfresco.rest.framework.webscripts.AbstractResourceWebScript;
import org.alfresco.rest.framework.webscripts.ApiWebScript;
import org.alfresco.rest.framework.webscripts.ResourceWebScriptDelete;
import org.alfresco.rest.framework.webscripts.WithResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Format;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tests the interactions with WithResponse
 *
 * @author Gethin James
 */
public class WithResponseTest
{

    @Test
    public void testDefaults() throws Exception
    {
        WithResponse callBack = new WithResponse(Status.STATUS_OK,ApiWebScript.DEFAULT_JSON_CONTENT, ApiWebScript.CACHE_NEVER);
        assertEquals(Status.STATUS_OK, callBack.getStatus());
        assertEquals(ApiWebScript.DEFAULT_JSON_CONTENT, callBack.getContentInfo());
        assertEquals(ApiWebScript.CACHE_NEVER, callBack.getCache());
        assertTrue(callBack.getHeaders().isEmpty());
    }

    @Test
    public void testSetHeader() throws Exception
    {
        WithResponse callBack = new WithResponse(Status.STATUS_OK,ApiWebScript.DEFAULT_JSON_CONTENT, ApiWebScript.CACHE_NEVER);
        callBack.setHeader("king", "can");
        callBack.setHeader("king", "kong");
        assertTrue(callBack.getHeaders().size() == 1);
        List<String> vals = callBack.getHeaders().get("king");
        assertTrue(vals.size() == 1);
        assertEquals("kong", vals.get(0));
    }

    @Test
    public void testAddHeader() throws Exception
    {
        WithResponse callBack = new WithResponse(Status.STATUS_OK,ApiWebScript.DEFAULT_JSON_CONTENT, ApiWebScript.CACHE_NEVER);
        callBack.addHeader("king", "can");
        callBack.addHeader("king", "kong");
        assertTrue(callBack.getHeaders().size() == 1);
        List<String> vals = callBack.getHeaders().get("king");
        assertTrue(vals.size() == 2);
        assertEquals(vals, Arrays.asList("can", "kong"));
    }


    @Test
    public void testSetters() throws Exception
    {
        WithResponse callBack = new WithResponse(Status.STATUS_OK, ApiWebScript.DEFAULT_JSON_CONTENT, ApiWebScript.CACHE_NEVER);
        callBack.setStatus(Status.STATUS_GONE);
        Cache myCache = new Cache(new Description.RequiredCache()
        {
            @Override
            public boolean getNeverCache()
            {
                return false;
            }

            @Override
            public boolean getIsPublic()
            {
                return true;
            }

            @Override
            public boolean getMustRevalidate()
            {
                return true;
            }
        });
        callBack.setCache(myCache);
        ContentInfo myContent = new ContentInfoImpl(Format.HTML.mimetype(),"UTF-16", 12, Locale.FRENCH);
        callBack.setContentInfo(myContent);

        assertEquals(Status.STATUS_GONE, callBack.getStatus());
        assertEquals(myCache, callBack.getCache());
        assertEquals(myContent, callBack.getContentInfo());
    }


    @Test
    public void testSetResponse() throws Exception
    {
        AbstractResourceWebScript responseWriter = new ResourceWebScriptDelete();
        WithResponse wr = new WithResponse(Status.STATUS_OK, ApiWebScript.DEFAULT_JSON_CONTENT, ApiWebScript.CACHE_NEVER);

        WebScriptResponse response = mock(WebScriptResponse.class);

        responseWriter.setResponse(response,wr.getStatus(), wr.getCache(), wr.getContentInfo(), wr.getHeaders());
        verify(response, times(1)).setStatus(anyInt());
        verify(response, times(1)).setCache((Cache) any());
        verify(response, times(1)).setContentType(anyString());
        verify(response, times(0)).setHeader(anyString(), anyString());

        response = mock(WebScriptResponse.class);

        responseWriter.setResponse(response,wr.getStatus(), null, null, null);
        verify(response, times(1)).setStatus(anyInt());
        verify(response, times(0)).setCache((Cache) any());
        verify(response, times(0)).setContentType(anyString());
        verify(response, times(0)).setHeader(anyString(), anyString());

        response = mock(WebScriptResponse.class);

        wr.addHeader("king", "can");
        wr.addHeader("king", "kong");
        responseWriter.setResponse(response,wr.getStatus(), null, null, wr.getHeaders());
        verify(response, times(1)).setStatus(anyInt());
        verify(response, times(0)).setCache((Cache) any());
        verify(response, times(0)).setContentType(anyString());
        verify(response, times(1)).setHeader(eq("king"), anyString());
        verify(response, times(1)).addHeader(eq("king"), anyString());

        response = mock(WebScriptResponse.class);

        wr.addHeader("king", "kin");
        wr.setHeader("ping", "ping");
        responseWriter.setResponse(response,wr.getStatus(), null, null, wr.getHeaders());
        verify(response, times(1)).setStatus(anyInt());
        verify(response, times(0)).setCache((Cache) any());
        verify(response, times(0)).setContentType(anyString());
        verify(response, times(1)).setHeader(eq("king"), anyString());
        verify(response, times(1)).setHeader(eq("ping"), anyString());
        verify(response, times(2)).addHeader(eq("king"), anyString());


    }

}