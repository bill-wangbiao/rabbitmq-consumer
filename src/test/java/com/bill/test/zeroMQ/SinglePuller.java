package com.bill.test.zeroMQ;

import java.io.UnsupportedEncodingException;

import org.zeromq.ZMQ;

public class SinglePuller {
	public static void main(String[] args) {
		ZMQ.Context context=ZMQ.context(1);
		ZMQ.Socket pull=context.socket(ZMQ.PULL);
		
		pull.bind("ipc://bill");
		
		int number=0;
		while(true) {
			String message="";
			try {
				System.out.println("******pull端正在pull。。。。。******");
				message=new String(pull.recv(),"utf-8");
				System.out.println("************接收到的数据："+message);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			number++;
			if(number%100000==0) {
				System.out.println("*********number:"+number);
			}
		}
	}
}
