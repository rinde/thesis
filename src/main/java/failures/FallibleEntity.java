package failures;

public interface FallibleEntity {
	public void setFailureModel(FailureModel model);
	public boolean isFailing();
//	public void printState();
}
