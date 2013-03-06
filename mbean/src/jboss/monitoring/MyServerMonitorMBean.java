package jboss.monitoring;

public interface MyServerMonitorMBean {
	public void setFrequency(String frequency);

	public String getFrequency();
}
