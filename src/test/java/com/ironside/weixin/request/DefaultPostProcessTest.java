package com.ironside.weixin.request;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ironside.weixin.request.entity.AbstractBaseEntity;
import com.ironside.weixin.request.entity.EntityType;
import com.ironside.weixin.request.entity.EventScanSubscribeEntity;
import com.ironside.weixin.request.entity.EventSubscribeEntity;
import com.ironside.weixin.request.entity.ImageEntity;

/**
 * POST方式推送给微信公众账号的消息处理测试
 * @author 雪庭
 * @sine 1.0 at 2015年4月14日
 */
public class DefaultPostProcessTest {
	
	/** POST方式推送给微信公众账号的消息处理 */
	DefaultPostProcess process;

	@Before
	public void setUp() throws Exception {
		process = new DefaultPostProcess();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 测试解析普通消息
	 */
	@Test
	public void testAnalyzeMessage() {
		// 构造xml
		String xml = "<xml>" +
				"<ToUserName><![CDATA[toUser]]></ToUserName>" +
				"<FromUserName><![CDATA[fromUser]]></FromUserName>" +
				"<CreateTime>1348831860</CreateTime>" +
				"<MsgType><![CDATA[image]]></MsgType>" +
				"<PicUrl><![CDATA[this is a url]]></PicUrl>" +
				"<MediaId><![CDATA[media_id]]></MediaId>" +
				"<MsgId>1234567890123456</MsgId>" +
				"</xml>";
		// 调用解析
		AbstractBaseEntity entity = process.analyze(xml);
		// 验证结果
		Assert.assertEquals(entity.getMsgType(), EntityType.IMAGE);
		Assert.assertEquals("toUser", entity.getToUserName());
		Assert.assertTrue(entity instanceof ImageEntity);
		ImageEntity iEntity = (ImageEntity)entity;
		Assert.assertEquals("this is a url", iEntity.getPicUrl()); 
	}
	
	/**
	 * 测试解析事件消息
	 */
	@Test
	public void testAnalyzeEvent() {
		/** 测试关注/取消关注-订阅事件 */
		// 构造xml
		String xml = 
				"<xml>" +
				"<ToUserName><![CDATA[toUser]]></ToUserName>" +
				"<FromUserName><![CDATA[FromUser]]></FromUserName>" +
				"<CreateTime>123456789</CreateTime>" +
				"<MsgType><![CDATA[event]]></MsgType>" +
				"<Event><![CDATA[subscribe]]></Event>" +
				"</xml>";
		// 调用解析
		AbstractBaseEntity entity = process.analyze(xml);
		EventSubscribeEntity sEntity = (EventSubscribeEntity)entity;
		Assert.assertEquals(sEntity.getMsgType(), EntityType.EVENT);
		Assert.assertEquals(sEntity.getEvent(), EntityType.EVENT_SUBSCRIBE);
		Assert.assertNull(entity.getEventKey());
		Assert.assertEquals("toUser", sEntity.getToUserName());
		/** 扫描带参数二维码-用户未关注时，进行关注后的事件 */
		// 构造xml
		xml = 
				"<xml>" +
				"<ToUserName><![CDATA[toUser]]></ToUserName>" +
				"<FromUserName><![CDATA[FromUser]]></FromUserName>" +
				"<CreateTime>123456789</CreateTime>" +
				"<MsgType><![CDATA[event]]></MsgType>" +
				"<Event><![CDATA[subscribe]]></Event>" +
				"<EventKey><![CDATA[qrscene_123123]]></EventKey>" +
				"<Ticket><![CDATA[TICKET]]></Ticket>" +
				"</xml>";
		// 调用解析
		entity = process.analyze(xml);
		// 验证结果
		Assert.assertEquals(entity.getEvent(), EntityType.EVENT_SUBSCRIBE);
		Assert.assertEquals(entity.getEventKey(), "qrscene_123123");
		EventScanSubscribeEntity ssEntity = (EventScanSubscribeEntity)entity;
		Assert.assertEquals(ssEntity.getMsgType(), EntityType.EVENT);
		Assert.assertEquals("toUser", ssEntity.getToUserName());
	}

}
