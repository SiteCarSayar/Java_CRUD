package com.example.demo;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
	}
	private final String secretKey = "vwx77ei2lAzpuSTLAckV0cY74xWg0k9XaFxBhWbS9h3dhbS2UoUqiGNsC8doA+V5QidGSXDZtdaYFEYW1UZZZw==";

	// Token validity in minutes (3 hours = 180 minutes)
	private final long allowTokenAliveTime = 180;
	/*
	 * public String generateToken(String username) { byte[] keyBytes =
	 * Base64.getDecoder().decode(secretKey); Key key =
	 * Keys.hmacShaKeyFor(keyBytes);
	 * 
	 * long nowMillis = System.currentTimeMillis(); long expMillis = nowMillis +
	 * allowTokenAliveTime * 60 * 1000; // convert minutes → ms Date exp = new
	 * Date(expMillis);
	 * 
	 * return Jwts.builder() .setSubject(username) .setIssuedAt(new Date(nowMillis))
	 * .setExpiration(exp) .signWith(key, SignatureAlgorithm.HS256) .compact(); }
	 */
}
