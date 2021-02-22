package com.ifocus.trackun.seikoits.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ifocus.trackun.seikoits.config.IotPfUrlConfig;
import com.ifocus.trackun.seikoits.config.IotPfUserConfig;
import com.ifocus.trackun.seikoits.entity.Seikoits_userEntity;
import com.ifocus.trackun.seikoits.model.BatchOperationResult;
import com.ifocus.trackun.seikoits.model.Device;
import com.ifocus.trackun.seikoits.model.DeviceStatus;
import com.ifocus.trackun.seikoits.model.PaginationList;
import com.ifocus.trackun.seikoits.model.PfToken;

@Component
public class IotPFService {
	
	public static final String TOKEN_PREFIX = "Bearer ";
	
	public static String getRawToken(String bearerToken) {
		return bearerToken.replaceFirst(TOKEN_PREFIX, "");
	}
	
	public static String getBearerToken(String token) {
		return TOKEN_PREFIX + token;
	}
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private IotPfUserConfig iotPfUserConfig;
	
	@Autowired
	private IotPfUrlConfig urlConfig;
	
	@Autowired
	private GroupService groupService;
	
	public PfToken getPfToken() {
		PfToken pfToken = null;
		
		HttpPost request = new HttpPost(urlConfig.getTokenFetchUrl());
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List <BasicNameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("grant_type", "password"));
		parameters.add(new BasicNameValuePair("client_id", "trackun"));
		parameters.add(new BasicNameValuePair("username", iotPfUserConfig.getUsername())); 
		parameters.add(new BasicNameValuePair("password", iotPfUserConfig.getPassword()));
		try {
			request.setEntity(new UrlEncodedFormEntity(parameters));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                pfToken = new Gson().fromJson(responseBody, PfToken.class);
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return pfToken;
	}

	public PfToken refreshPfToken(Seikoits_userEntity user, String refreshToken) {
		PfToken pfToken = null;
		
		HttpPost request = new HttpPost(urlConfig.getTokenUpdateUrl());
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List <BasicNameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
		parameters.add(new BasicNameValuePair("client_id", "trackun")); // TODO
		parameters.add(new BasicNameValuePair("refresh_token", refreshToken));
		try {
			request.setEntity(new UrlEncodedFormEntity(parameters));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                pfToken = new Gson().fromJson(responseBody, PfToken.class);
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return pfToken;
	}
	
	public BatchOperationResult deviceBatchAdd(Seikoits_userEntity user, List<Device> devices) {
		BatchOperationResult result = null;
		
		HttpPut request = new HttpPut(urlConfig.getDeviceBatchBindUrl());
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		request.setHeader("Content-Type", "application/json");
		JsonObject requestBody = new JsonObject();
		requestBody.add("list", new Gson().toJsonTree(devices));
		request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            String responseBody = null;
            if (response.getEntity() != null) {
        		responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			}
            if (status == HttpStatus.SC_CREATED || status == HttpStatus.SC_PARTIAL_CONTENT){                
                if (responseBody != null) {
                	result = new Gson().fromJson(responseBody, BatchOperationResult.class);
				}
            }else {
            	LOG.warn("Fail to batchAdd device, status code={}, detail: {}", status, responseBody);
            	BatchOperationResult batchOperationResult = new BatchOperationResult();
            	for (Device device : devices) {
            		batchOperationResult.getFailImeis().add(device.getImei());
            		batchOperationResult.setFailCount(batchOperationResult.getFailImeis().size());
				}
				result = batchOperationResult;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}

	public boolean deviceModify(Seikoits_userEntity user, Device device) {
		boolean updated = false;
		
		HttpPost request = new HttpPost(urlConfig.getDeviceBindModifyUrl());
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		request.setHeader("Content-Type", "application/json");
		
		JsonElement requestBody = new Gson().toJsonTree(device);
		request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode(); 
            if (status == HttpStatus.SC_OK || status == HttpStatus.SC_RESET_CONTENT){                
            	updated = true;
            }else {
				LOG.warn("Device(imei={}) modify fail. detail: {}", device.getImei(), EntityUtils.toString(response.getEntity()));
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return updated;
	}

	public BatchOperationResult deviceBatchDelete(Seikoits_userEntity user, List<String> deleteTargets) {
		BatchOperationResult batchOperationResult = new BatchOperationResult();
		
		List<String> validDeleteTargets = new ArrayList<>();
		List<String> unvalidImeis = new ArrayList<>();
		for (String imei : deleteTargets) {
			if (isUserValidForDevice(user, imei)) {
				validDeleteTargets.add(imei);
			}else {
				unvalidImeis.add(imei);
			}
		}

		HttpPost request = new HttpPost(urlConfig.getDeviceBatchUnbindUrl());
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		request.setHeader("Content-Type", "application/json");

		JsonObject requestBody = new JsonObject();
		requestBody.add("deleteTargets", new Gson().toJsonTree(validDeleteTargets));
		request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            String responseBody = null;
            if (response.getEntity() != null) {
        		responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			}
            if (status == HttpStatus.SC_OK){          
            	if (responseBody != null) {
            		 Gson gson = new Gson();
                     BatchOperationResult resultFromPf = gson.fromJson(responseBody, BatchOperationResult.class);
                     batchOperationResult.setSuccessImeis(resultFromPf.getSuccessImeis());
                     batchOperationResult.setSuccessCount(resultFromPf.getSuccessImeis().size());
                     List<String> failImeis = resultFromPf.getFailImeis();
                     failImeis.addAll(unvalidImeis);
                     batchOperationResult.setFailImeis(failImeis);
                     batchOperationResult.setFailCount(failImeis.size());
				}
            }else {
            	LOG.warn("Fail to batch delete device, status code={}, detail: {}", status, responseBody);
            	batchOperationResult.getFailImeis().addAll(deleteTargets);
        		batchOperationResult.setFailCount(batchOperationResult.getFailImeis().size());
			}
            
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return batchOperationResult;
	}

	public boolean isUserValidForDevice(Seikoits_userEntity user, String imei) {
		Device device = new Device();
		device.setImei(imei);
		PaginationList<Device> list = deviceList(user, device, 1, 1);
		return list.getTotal() > 0;
	}

	public PaginationList<Device> deviceList(Seikoits_userEntity user, Device device, int pageSize, int pageNumber) {
		PaginationList<Device> paginationList = new PaginationList<>(pageSize, pageNumber);
		
		StringBuffer urlBuffer = new StringBuffer(urlConfig.getDeviceBindListUrl());
		urlBuffer.append("?pageSize=");
		urlBuffer.append(pageSize);
		urlBuffer.append("&"); // TODO
		urlBuffer.append("pageNumber=");
		urlBuffer.append(pageNumber);
		urlBuffer.append("&"); 
		urlBuffer.append("bifReturn=true");
		if (device != null) {
			if (device.getImei() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("imei=");
				urlBuffer.append(device.getImei());
			}
			if (device.getIccid() != null) {
				urlBuffer.append("&"); 
				urlBuffer.append("iccid=");
				urlBuffer.append(device.getIccid());
			}
			if (device.getDevicename() != null) {
				urlBuffer.append("&"); 
				urlBuffer.append("devicename=");
				urlBuffer.append(device.getDevicename());
			}
			if (device.getImsi() != null) {
				urlBuffer.append("&"); 
				urlBuffer.append("imsi=");
				urlBuffer.append(device.getImsi());
			}
		}
		
		Map<String, String> exFieldsForDevice = groupService.composeExFieldsForDevice(user);
		if (exFieldsForDevice.size() > 0) {
			for (Entry<String, String> entry : exFieldsForDevice.entrySet()) {
				urlBuffer.append("&");
				urlBuffer.append("exFields.");
				urlBuffer.append(entry.getKey());
				urlBuffer.append("=");
				urlBuffer.append(entry.getValue());
			}
		}
		
		String url = urlBuffer.toString();
		HttpGet request = new HttpGet(url);
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		request.setHeader("Content-Type", "application/json");
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                JsonObject jsonObj = gson.fromJson(responseBody, JsonObject.class);
                paginationList.setTotal(new Long(jsonObj.get("total").toString()));
                List<Device> devices = new ArrayList<>();
                for (JsonElement jsonElement : jsonObj.get("list").getAsJsonArray()) {
					devices.add(gson.fromJson(jsonElement, Device.class));
				}
                paginationList.setCurrentPage(devices);
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return paginationList;
	}

	public PaginationList<DeviceStatus> deviceStatusList(Seikoits_userEntity user, Device device, int pageSize, int pageNumber) {
		PaginationList<DeviceStatus> paginationList = new PaginationList<>(pageSize, pageNumber);

		StringBuffer urlBuffer = new StringBuffer(urlConfig.getDeviceStatusListUrl());
		urlBuffer.append("?pageSize=");
		urlBuffer.append(pageSize);
		urlBuffer.append("&"); // TODO
		urlBuffer.append("pageNumber=");
		urlBuffer.append(pageNumber);
		urlBuffer.append("&"); 
		urlBuffer.append("returnBif=");
		urlBuffer.append(true);
 
		if (device != null) {
			if (device.getImei() != null) {
				urlBuffer.append("&"); 
				urlBuffer.append("imei=");
				urlBuffer.append(device.getImei());
			}
			if (device.getIccid() != null) {
				urlBuffer.append("&"); 
				urlBuffer.append("bif.iccid=");
				urlBuffer.append(device.getIccid());
			}
			if (device.getDevicename() != null) {
				urlBuffer.append("&"); 
				urlBuffer.append("bif.devicename=");
				urlBuffer.append(device.getDevicename());
			}
			if (device.getImsi() != null) {
				urlBuffer.append("&"); 
				urlBuffer.append("bif.imsi=");
				urlBuffer.append(device.getImsi());
			}
		}
		
		Map<String, String> exFieldsForDevice = groupService.composeExFieldsForDevice(user);
		if (exFieldsForDevice.size() > 0) {
			for (Entry<String, String> entry : exFieldsForDevice.entrySet()) {
				urlBuffer.append("&");
				urlBuffer.append("exFields.");
				urlBuffer.append(entry.getKey());
				urlBuffer.append("=");
				urlBuffer.append(entry.getValue());
			}
		}

		
		String url = urlBuffer.toString();
		HttpGet request = new HttpGet(url);
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		request.setHeader("Content-Type", "application/json");
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                JsonObject jsonObj = gson.fromJson(responseBody, JsonObject.class);
                paginationList.setTotal(new Long(jsonObj.get("total").toString()));
                List<DeviceStatus> devices = new ArrayList<>();
                for (JsonElement jsonElement : jsonObj.get("list").getAsJsonArray()) {
					devices.add(gson.fromJson(jsonElement, DeviceStatus.class));
				}
                paginationList.setCurrentPage(devices);
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return paginationList;
	}
	
}
