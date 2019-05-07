package com.github.stuartraetaylor.punkapiexport.punkapi;

import static com.github.stuartraetaylor.punkapiexport.punkapi.PunkAPIUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stuartraetaylor.punkapiexport.*;
import com.github.stuartraetaylor.punkapiexport.punkapi.model.PunkSchema;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PunkAPIReader implements PunkReader {

    private static final String baseURL = "https://api.punkapi.com/v2/";

	@Override
    public PunkDocument read(String beerName) throws PunkException {
        try {
            String formattedName = formatBeerName(beerName);

            URI uri = new URIBuilder(baseURL + "beers")
                .addParameter("beer_name", formattedName)
                .build();

            log.debug("Requesting: {}", uri);

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(uri);
            request.addHeader("accept", "application/json");

            HttpResponse response = client.execute(request);
            log.debug(response.getFirstHeader("X-RateLimit-Remaining"));

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new PunkException("HTTP Request Failed: " + response.getStatusLine());
            }

            PunkSchema document = fromInputStream(response.getEntity().getContent());
            return new PunkDocument(formattedName, document);
        } catch (IOException | URISyntaxException e) {
            throw new PunkException("PunkAPI read failed", e);
		}
    }

	@Override
	public List<PunkDocument> readAll() throws PunkException {
        throw new UnsupportedOperationException();
	}

    static PunkSchema fromInputStream(InputStream in) throws PunkException {
        try {
            List<PunkSchema> document = new ObjectMapper().readValue(in, new TypeReference<List<PunkSchema>>() {});
            if (document.isEmpty())
                throw new PunkException("Beer not found");

            return document.get(0);
		} catch (IOException e) {
            throw new PunkException("Failed to parse PunkAPI data", e);
		}
    }

    private final Logger log = LogManager.getLogger(this.getClass());

}
