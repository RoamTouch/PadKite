package com.roamtouch.utils;

import java.util.*;
import java.lang.*;
import java.net.*;

public class GetDomainName
{
  
	public void GetDomainName() {
	
	}
	  
	  
	public String getDomain(String urlAddress){
		String domain = null;
		  
		  try{		  
			  
			  URL url = new URL(urlAddress);
			  domain = url.getHost();
			 
		  }catch (Exception e){
			  
			  System.out.println("Exception caught ="+e.getMessage());
		  }
		  return domain;
	}
	  
	
	  
}
