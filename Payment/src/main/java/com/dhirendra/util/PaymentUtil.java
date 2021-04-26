package com.dhirendra.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaymentUtil {

	/**
	 * fetch the requestbody
	 * 
	 * @param req
	 * @return
	 */
	public static String getRequestBody(HttpServletRequest req) {
		try {
			BufferedReader reader = req.getReader();
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String json = sb.toString();
			return json;
		} catch (IOException e) {
			log.info("body" + e.getMessage());
		}
		return "";
	}

	/**
	 * convert String json to Object type
	 * 
	 * @param json
	 * @return
	 */
	public static Object getModelObject(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, Object.class);

	}

	/**
	 * Sum of digits in String
	 * 
	 * @param account
	 * @return
	 */

	public static Integer sum(String account) {

		return Arrays.stream(account.split("")).filter((s) -> s.matches("\\d+")).mapToInt(Integer::valueOf).sum();
	}

	public static String getServletResponseAsString(ServletResponse response) {
		Gson gson = new Gson();
		return gson.toJson(response, Object.class);
	}
}