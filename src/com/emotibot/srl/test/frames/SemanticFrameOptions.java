package com.emotibot.srl.test.frames;

public class SemanticFrameOptions {

	public boolean useGeneralFrame;
	public boolean useDomainSpecificFrame;
	
	public SemanticFrameOptions() {
		useGeneralFrame = true;
		useDomainSpecificFrame = true;
	}

	/**
	 * @return the useGeneralFrame
	 */
	public boolean isUseGeneralFrame() {
		return useGeneralFrame;
	}

	/**
	 * @param useGeneralFrame
	 *            the useGeneralFrame to set
	 */
	public void setUseGeneralFrame(boolean useGeneralFrame) {
		this.useGeneralFrame = useGeneralFrame;
	}

	/**
	 * @return the useDomainSpecificFrame
	 */
	public boolean isUseDomainSpecificFrame() {
		return useDomainSpecificFrame;
	}

	/**
	 * @param useDomainSpecificFrame
	 *            the useDomainSpecificFrame to set
	 */
	public void setUseDomainSpecificFrame(boolean useDomainSpecificFrame) {
		this.useDomainSpecificFrame = useDomainSpecificFrame;
	}
}
