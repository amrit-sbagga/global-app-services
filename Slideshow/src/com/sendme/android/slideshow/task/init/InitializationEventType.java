package com.sendme.android.slideshow.task.init;

/**
 *
 * @author Todd Tidwell <ttidwell@sendme.com>
 */
public enum InitializationEventType
{
	INITIALIZING,
	DATABASE_INITIALIZING,
	DATABASE_INITIALIZED,
	DATABASE_ERROR,
	LOCAL_IMAGE_SOURCE_INITIALIZING,
	LOCAL_IMAGE_SOURCE_INITIALIZED,
	LOCAL_IMAGE_SOURCE_ERROR,
	FACEBOOK_IMAGE_SOURCE_INITIALIZING,
	FACEBOOK_IMAGE_SOURCE_AUTHORIZATION,
	FACEBOOK_IMAGE_SOURCE_AUTHORIZATION_ERROR,
	FACEBOOK_IMAGE_SOURCE_AUTHORIZATION_CANCELLED,
	FACEBOOK_IMAGE_SOURCE_AUTHORIZED,
	FACEBOOK_IMAGE_SOURCE_INITIALIZED,
	FACEBOOK_IMAGE_SOURCE_ERROR,
	LOCAL_AUDIO_TRACKS_SOURCE_INITIALIZING,
	LOCAL_AUDIO_TRACKS_SOURCE_INITIALIZED,
	LOCAL_AUDIO_TRACKS_SOURCE_ERROR,
	SLIDESHOW_INITIALIZING,
	SLIDESHOW_INITIALIZED,
	PLAYLIST_INITIALIZING,
	PLAYLIST_INITIALIZED,
	INITIALIZED
}
