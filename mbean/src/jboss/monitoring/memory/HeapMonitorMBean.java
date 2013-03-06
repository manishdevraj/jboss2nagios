package jboss.monitoring.memory;

public interface HeapMonitorMBean {
	public void setFrequency(String frequency);

	public String getFrequency();
}
