package com.ifocus.trackun.seikoits.service;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

	public PfToken getPfToken() {
		PfToken pfToken = null;
		
		String url = "https://auth.aaascloud.io/v1.0/user/login";
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List <BasicNameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("grant_type", "password"));
		parameters.add(new BasicNameValuePair("client_id", "trackun"));
		parameters.add(new BasicNameValuePair("username", "itcs")); // TODO
		parameters.add(new BasicNameValuePair("password", "123456"));
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

	public PfToken refreshPfToken(Seikoits_userEntity user, String token) {
		PfToken pfToken = null;
		
		String url = "https://auth.aaascloud.io/v1.0/user/login";
		HttpPost request = new HttpPost(url);
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List <BasicNameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("grant_type", "refresh_token"));
		parameters.add(new BasicNameValuePair("client_id", "trackun")); // TODO
		parameters.add(new BasicNameValuePair("refresh_token", token));
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
		
		// TODO user's filter add to extends fields
		Map<String, String> exFields = new HashMap<>();
		exFields.put("itcs_user_id", user.getUserid().toString()); // TODO
		for (Device device : devices) {
			device.setExFields(exFields);
		}
		
		String url = "https://demo.trackun.jp/v1.0/deviceBinding/batchBind"; // TODO
		HttpPut request = new HttpPut(url);
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		JsonObject requestBody = new JsonObject();
		requestBody.add("list", new Gson().toJsonTree(devices));
		request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                result = new Gson().fromJson(responseBody, BatchOperationResult.class);
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return result;
	}

	public Device deviceModify(Seikoits_userEntity user, Device device) {
		Device updated = null;
		
		// TODO user's filter add to extends fields
		Map<String, String> exFields = new HashMap<>();
		exFields.put("itcs_user_id", user.getUserid().toString()); // TODO
		
		device.setExFields(exFields);
		
		String url = "https://demo.trackun.jp/v1.0/deviceBinding/modify"; // TODO
		HttpPost request = new HttpPost(url);
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		JsonElement requestBody = new Gson().toJsonTree(device);
		request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Map<?, ?> map = gson.fromJson(responseBody, Map.class);
                updated = gson.fromJson(gson.toJsonTree(map.get("data")), Device.class);
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
			// check valid
			Device device = new Device();
			device.setImei(imei);
			PaginationList<Device> list = deviceList(user, device, 1, 1);
			// remove
			if (list.getTotal() > 0) {
				validDeleteTargets.add(imei);
			}else {
				unvalidImeis.add(imei);
			}
		}
		
		String url = "https://demo.trackun.jp/v1.0/deviceBinding/batchUnbind"; // TODO
		HttpPost request = new HttpPost(url);
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		JsonObject requestBody = new JsonObject();
		requestBody.add("deleteTargets", new Gson().toJsonTree(validDeleteTargets));
		request.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                BatchOperationResult resultFromPf = gson.fromJson(responseBody, BatchOperationResult.class);
                batchOperationResult.setSuccessImeis(resultFromPf.getSuccessImeis());
                batchOperationResult.setSuccessCount(resultFromPf.getSuccessImeis().size());
                List<String> failImeis = resultFromPf.getFailImeis();
                failImeis.addAll(unvalidImeis);
                batchOperationResult.setFailImeis(failImeis);
                batchOperationResult.setFailCount(failImeis.size());
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return batchOperationResult;
	}

	public PaginationList<Device> deviceList(Seikoits_userEntity user, Device device, int pageSize, int pageNumber) {
		PaginationList<Device> paginationList = new PaginationList<>(pageSize, pageNumber);
		
		StringBuffer urlBuffer = new StringBuffer("https://demo.trackun.jp/v1.0/deviceBinding/list?pageSize="); // TODO
		urlBuffer.append(pageSize);
		urlBuffer.append("&"); // TODO
		urlBuffer.append("pageNumber=");
		urlBuffer.append(pageNumber);
		if (device != null) {
			if (device.getImei() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("imei=");
				urlBuffer.append(device.getImei());
			}
			if (device.getIccid() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("iccid=");
				urlBuffer.append(device.getIccid());
			}
			if (device.getDevicename() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("devicename=");
				urlBuffer.append(device.getDevicename());
			}
			if (device.getImsi() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("imsi=");
				urlBuffer.append(device.getImsi());
			}
		}
		
		// TODO user's filter add to extends fields
		urlBuffer.append("&"); // TODO
		urlBuffer.append("exFields.itcs_user_id=");
		urlBuffer.append(user.getUserid().toString());
		
		String url = urlBuffer.toString();
		HttpGet request = new HttpGet(url);
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Map<?, ?> map = gson.fromJson(responseBody, Map.class);
                paginationList.setTotal(new Integer(map.get("total").toString()));
                paginationList.setCurrentPage(gson.fromJson(gson.toJsonTree(map.get("list")), new ParameterizedType() {
					
					@Override
					public Type getRawType() {
						return Device.class;
					}
					
					@Override
					public Type getOwnerType() {
						return new ArrayList<Device>().getClass();
					}
					
					@Override
					public Type[] getActualTypeArguments() {
						return new Type[] {new ArrayList<Device>().getClass()};
					}
				}));
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return paginationList;
	}

	public PaginationList<DeviceStatus> deviceStatusList(Seikoits_userEntity user, Device device, int pageSize, int pageNumber) {
		PaginationList<DeviceStatus> paginationList = new PaginationList<>(pageSize, pageNumber);
		
		StringBuffer urlBuffer = new StringBuffer("https://demo.trackun.jp/v1.0/deviceStatus/list?pageSize="); // TODO
		urlBuffer.append(pageSize);
		urlBuffer.append("&"); // TODO
		urlBuffer.append("pageNumber=");
		urlBuffer.append(pageNumber);
		if (device != null) {
			if (device.getImei() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("imei=");
				urlBuffer.append(device.getImei());
			}
			if (device.getIccid() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("bif.iccid=");
				urlBuffer.append(device.getIccid());
			}
			if (device.getDevicename() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("bif.devicename=");
				urlBuffer.append(device.getDevicename());
			}
			if (device.getImsi() != null) {
				urlBuffer.append("&"); // TODO
				urlBuffer.append("bif.imsi=");
				urlBuffer.append(device.getImsi());
			}
		}
		
		// TODO user's filter add to extends fields
		urlBuffer.append("&"); // TODO
		urlBuffer.append("exFields.itcs_user_id=");
		urlBuffer.append(user.getUserid().toString());
		
		String url = urlBuffer.toString();
		HttpGet request = new HttpGet(url);
		request.setHeader("Authorization", getBearerToken(user.getToken()));
		
		try(CloseableHttpClient httpclient = HttpClients.createDefault();
				CloseableHttpResponse response = httpclient.execute(request);) {
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK){                
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                Gson gson = new Gson();
                Map<?, ?> map = gson.fromJson(responseBody, Map.class);
                paginationList.setTotal(new Integer(map.get("total").toString()));
                paginationList.setCurrentPage(gson.fromJson(gson.toJsonTree(map.get("list")), new ParameterizedType() {
					
					@Override
					public Type getRawType() {
						return DeviceStatus.class;
					}
					
					@Override
					public Type getOwnerType() {
						return new ArrayList<DeviceStatus>().getClass();
					}
					
					@Override
					public Type[] getActualTypeArguments() {
						return new Type[] {new ArrayList<DeviceStatus>().getClass()};
					}
				}));
            }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return paginationList;
	}
	
}
