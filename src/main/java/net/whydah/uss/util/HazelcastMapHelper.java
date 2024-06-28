package net.whydah.uss.util;

import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.cluster.Member;
import com.hazelcast.collection.IList;
import com.hazelcast.collection.ItemListener;
import com.hazelcast.config.AwsConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.MapListener;

public class HazelcastMapHelper {

	private final static Logger log = LoggerFactory.getLogger(HazelcastMapHelper.class);
	static Config hazelcastConfig;
	static String tag ="";
	private static HazelcastInstance hazelcastInstance;
	
	static {
		
		hazelcastInstance = Hazelcast.newHazelcastInstance();
		
		try {
			AwsConfig awsConfig = hazelcastInstance.getConfig().getNetworkConfig().getJoin().getAwsConfig();
			if(awsConfig!=null) {
				tag = awsConfig.getProperty("tag-value")!=null?awsConfig.getProperty("tag-value"):"";
			} else {
				tag = "local";
			}
			
		} catch(Exception ex) {

		}
	}

	public static IMap register(String name, MapListener listener) {
		log.info("Connectiong to map {}", tag + "_" + name);		
		IMap result = hazelcastInstance.getMap(tag + "_" + name);
		if(listener!=null) {
			result.addEntryListener(listener, true);
		}
		return result;

	}
	
	public static IList registerList(String name, ItemListener listener) {

		log.info("Connectiong to map {}", tag + "_" + name);
		
		IList result = hazelcastInstance.getList(tag + "_" + name);
		if(listener!=null) {
			result.addItemListener(listener, true);
		}
		return result;

	}
	
	
	public static IList registerList(String name) {
		return registerList(name, null);
	}
	
	public static IMap register(String name) {
		return register(name, null);
	}
	
	public static boolean isLeader() {
		Iterator<HazelcastInstance> iter = Hazelcast.getAllHazelcastInstances().iterator();

		if (iter.hasNext()) { // cluster mode 
			HazelcastInstance instance = iter.next();
			return instance.getCluster().getMembers().iterator().next().localMember();
		} else {
			return true; // standalone mode
		}
	}
	 
	public static String localMember() {
		try {
			return hazelcastInstance.getCluster().getLocalMember().getAddress().getInetAddress().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Set<String> getClusterMembers() {
		Set<String> inetAddresses = new HashSet<>();
		Set<Member> members = hazelcastInstance.getCluster().getMembers();
		members.stream().forEach(member -> {
			try {
				inetAddresses.add(member.getAddress().getInetAddress().getHostAddress());
			} catch (UnknownHostException e) {
				log.error("Unable to gather IP address from hazelcast member", e);
			}
		});
		return inetAddresses;
	}

	public static HazelcastInstance getHazelcastInstance() {
		return hazelcastInstance;
	}
}
