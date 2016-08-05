package per.chzopen.zkViewer.exception;

public class ShellException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	private ShellException(Throwable e)
	{
		super(e);
	}

	public static ShellException wrap(Throwable e)
	{
		if( e instanceof ShellException )
		{
			return (ShellException)e;
		}
		else
		{
			return new ShellException(e);
		}
	}
}
