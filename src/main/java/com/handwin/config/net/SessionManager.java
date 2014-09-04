package com.handwin.config.net;

import io.netty.channel.Channel;
import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import java.util.HashSet;
import java.util.Set;



/**
 * Session 管理模块
 * @author fangliang
 *
 */
public class SessionManager {
	
	private ConcurrentHashMapV8<Resources , Set<Channel>> resourceChannelCache = 
			new ConcurrentHashMapV8<Resources , Set<Channel>>() ;
	
	private ConcurrentHashMapV8<Channel, Set<Resources>> channelResourcesCache = 
			new ConcurrentHashMapV8<Channel, Set<Resources>>() ;
	
	private SessionManager() { } 
	
	private static SessionManager instance = new SessionManager() ;
	
	public static SessionManager getInstance() {
		return instance ;
	}
	
	public void subscribe(Resources resources, Channel channel  ) { 
		Set<Channel> set = null ;
		if(  ! resourceChannelCache.containsKey(resources)) { 
			set = new HashSet<Channel>();
			set.add( channel ) ;
			resourceChannelCache.put(resources, set ) ; 
		} else {
			set = resourceChannelCache.get( resources ) ;
			if( set == null ) {
				set = new HashSet<Channel>();
			}
			set.add( channel ) ; 
		}
		
		Set<Resources> resourceSet = null ;
		if( ! channelResourcesCache.containsKey(channel) ) {
			resourceSet = new HashSet<SessionManager.Resources>();
			resourceSet.add( resources ) ;
			channelResourcesCache.put(channel, resourceSet ) ; 
		}  else {
			resourceSet = channelResourcesCache.get( channel ) ; 
		}
	}
	
	public void unSubscribe(Resources resources , Channel channel ) {
		Set<Resources> resourceSet = channelResourcesCache.get( channel ) ;
		if( resourceSet != null ) {
			resourceSet.remove( resources ) ;
			if( resourceSet.size() == 0 ) {
				channelResourcesCache.remove(channel) ;
			}
			Set<Channel> channelSet = resourceChannelCache.get( resources ) ;
			channelSet.remove( channel ) ;
			if( channelSet.size() == 0 ) {
				resourceChannelCache.remove( resources) ;
 			}
		}
	}
	
	public void unSubscribeAll(Channel channel) {
		Set<Resources> resourceSet = channelResourcesCache.get( channel ) ;
		channelResourcesCache.remove( channel ) ;
		if( resourceSet != null ) {
			for( Resources resources : resourceSet ) {
				Set<Channel> channelSet = resourceChannelCache.get(resources) ;
				if( channelSet != null ) {
					channelSet.remove( channel ) ;
					if( channelSet.size() == 0 ) {
						resourceChannelCache.remove( resources ) ;
					}
				}
			}
		}
	}
	
	public Set<Channel> lookUp(Resources resources) {
		return resourceChannelCache.get( resources ) ; 
	}
	
	public static class Resources {
		
		private final String region ;
		private final String business ;
		
		public Resources(String region , String business) {
			this.region = region ;
			this.business = business ;
		}
		
		public String getBusiness() {
			return business;
		}
		public String getRegion() {
			return region;
		}
		
		@Override
		public boolean equals(Object obj) {
			if( obj == null ) {
				return false ;
			}
			if( obj == this ) {
				return true ;
			}
			if( ! ( obj instanceof Resources ) ) {
				return false ;
			}
			return this.hashCode() == obj.hashCode() ;
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode() ;
		}
		
		@Override
		public String toString() {
			return String.format("%s@%s", business , region ) ;
		}
		
		
	}
	
	
	
	

}
