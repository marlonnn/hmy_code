package com.BC.entertainment.config;

/**
 * 推流配置参数
 * @author wen zhong
 *
 */
public class PushConfig {

	public static final int CAMERA_POSITION_BACK = 0;
	public static final int CAMERA_POSITION_FRONT = 1;
	public static final int CAMERA_ORIENTATION_PORTRAIT = 0;
	public static final int CAMERA_ORIENTATION_LANDSCAPE = 1;
	public static final int LS_AUDIO_STREAMING_LOW_QUALITY = 0;
	public static final int LS_AUDIO_STREAMING_HIGH_QUALITY = 1;
	public static final int LS_AUDIO_CODEC_AAC = 0;
	public static final int LS_VIDEO_CODEC_AVC = 0;
	public static final int LS_AUDIO_CODEC_SPEEX = 1;
	public static final int LS_AUDIO_CODEC_MP3 = 2;
	public static final int LS_AUDIO_CODEC_G711A = 3;
	public static final int LS_AUDIO_CODEC_G711U = 4;
	public static final int FLV = 0;
	public static final int RTMP = 1;
	public static final int HAVE_AUDIO = 0;
	public static final int HAVE_VIDEO = 1;
	public static final int HAVE_AV = 2;
	public static final int LS_LOG_QUIET       = 0x00;            //!< log输出模式：不输出
    public static final int LS_LOG_ERROR       = 1 << 0;          //!< log输出模式：输出错误
    public static final int LS_LOG_WARNING     = 1 << 1;          //!< log输出模式：输出警告
    public static final int LS_LOG_INFO        = 1 << 2;          //!< log输出模式：输出信息
    public static final int LS_LOG_DEBUG       = 1 << 3;          //!< log输出模式：输出调试信息
    public static final int LS_LOG_DETAIL      = 1 << 4;          //!< log输出模式：输出详细
    public static final int LS_LOG_RESV        = 1 << 5;          //!< log输出模式：保留
    public static final int LS_LOG_LEVEL_COUNT = 6;
    public static final int LS_LOG_DEFAULT     = LS_LOG_WARNING;	//!< log输出模式：默认输出警告
}
