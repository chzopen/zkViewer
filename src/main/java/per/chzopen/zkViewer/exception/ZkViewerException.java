package per.chzopen.zkViewer.exception;

public class ZkViewerException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	public ZkViewerException(Throwable e)
	{
		super(e);
	}

	public ZkViewerException(String message)
	{
		super(message);
	}

	public ZkViewerException(String message, Throwable e)
	{
		super(message, e);
	}
	
}
