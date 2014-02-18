package org.denigma.video.models

import play.api.libs.json._


/**
 * ICE message
 * @param content
 */
case class VideoICE(content:JsValue)

/*
Offer to establish a connection
 */
case class VideoOffer(content:JsValue)

/**
 * Answer from another peer
 */
case class VideoAnswer(content:JsValue)