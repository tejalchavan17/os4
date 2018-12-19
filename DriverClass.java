import java.util.*;
import java.util.concurrent.Semaphore;
public class DriverClass
{
    
    static Semaphore mutex = new Semaphore(1);	//semaphore to perform mutual exclusion
    static Semaphore full = new Semaphore(0);	//semaphore to count full space in buffer
	static Semaphore empty = new Semaphore(5);	//semaphore to count empty space in buffer
	public static class CircularQueue
	{
	 	int buffer[]=new int[5];	//buffer
    	int in=0,out=0;
    	public void insertitem(int item) throws InterruptedException
    	{
       
	    	buffer[in]=item;
	    	System.out.println("\nproducer "+Thread.currentThread().getName()+" Adding in buffer item:"+item);
	    	in=(in+1)%5;
			/*Printing buffer*/
	    	System.out.println("After producing buffer:");
	    	for(int i=0;i<5;i++)
		    	System.out.print(buffer[i]+"\t");
		
    	}
    	public int removeitem() throws InterruptedException
    	{
        
	    	int item=buffer[out];
	   		 buffer[out]=0;
	    	System.out.println("\nconsumer "+Thread.currentThread().getName()+" Removing from buffer item:"+item);
	    	out=(out+1)%5;
			/*Printing buffer*/
	    	System.out.println("After consuming buffer:");
	    	for(int i=0;i<5;i++)
		    	System.out.print(buffer[i]+"\t");
       		return item;
		 
    	}
}
/*producer class*/
public static class Producer extends Thread
{
    CircularQueue cQueue ;  
	int i;
	static int item=1;
    public Producer(CircularQueue queue)
    {
        this.cQueue = queue;
		
    }
    public void run()
    {

        try
		{
			
			empty.acquire();
			mutex.acquire();					
    		cQueue.insertitem(item);	//inserting item into buffer
			item++;
			mutex.release();
			full.release();
	
		}
		catch (InterruptedException e) 
        {
            e.printStackTrace();
        }

    }
}
/*consumer class*/
public static class Consumer extends Thread
{
    CircularQueue cQueue ;
	int i;
   	public Consumer(CircularQueue cQueue)
    {
        this.cQueue = cQueue;
		
		
    }
    public void run()
    {
		try
		{
			
			full.acquire();
			mutex.acquire();
        	int it = cQueue.removeitem();	//removing item from buffer
      		mutex.release();
       		empty.release();
		
		}
		catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
	}
}
/*Main Class*/

    public static void main(String args[])
    {
        int pp,cp,s,i;
        CircularQueue cQueue = new CircularQueue();
		s=Integer.parseInt(args[0]);	//sleep time
        pp=Integer.parseInt(args[1]);	//total no. of producer process
        cp=Integer.parseInt(args[2]);	//total no. of consumer process
       	Producer mdp[]=new Producer[pp];
        Consumer mdc[]=new Consumer[cp];
        for(i=0;i<pp;i++)
        {
            mdp[i]=new Producer(cQueue);
            mdp[i].start();
			try
			{
				Thread.sleep(s);
			}
			catch(Exception e){}
			
        }
        for(i=0;i<cp;i++)
        {
            mdc[i]=new Consumer(cQueue);
            mdc[i].start();
			try
			{
				Thread.sleep(s);
			}
			catch(Exception e){}
			
        }
    }
}
/*Output
tejal@ubuntu:~/Desktop$ javac DriverClass.java
tejal@ubuntu:~/Desktop$ java DriverClass 500 8 6

producer Thread-0 Adding in buffer item:1
After producing buffer:
1	0	0	0	0	
producer Thread-1 Adding in buffer item:2
After producing buffer:
1	2	0	0	0	
producer Thread-2 Adding in buffer item:3
After producing buffer:
1	2	3	0	0	
producer Thread-3 Adding in buffer item:4
After producing buffer:
1	2	3	4	0	
producer Thread-4 Adding in buffer item:5
After producing buffer:
1	2	3	4	5	
consumer Thread-8 Removing from buffer item:1
After consuming buffer:
0	2	3	4	5	
producer Thread-5 Adding in buffer item:6
After producing buffer:
6	2	3	4	5	
consumer Thread-9 Removing from buffer item:2
After consuming buffer:
6	0	3	4	5	
producer Thread-6 Adding in buffer item:7
After producing buffer:
6	7	3	4	5	
consumer Thread-10 Removing from buffer item:3
After consuming buffer:
6	7	0	4	5	
producer Thread-7 Adding in buffer item:8
After producing buffer:
6	7	8	4	5	
consumer Thread-11 Removing from buffer item:4
After consuming buffer:
6	7	8	0	5	
consumer Thread-12 Removing from buffer item:5
After consuming buffer:
6	7	8	0	0	
consumer Thread-13 Removing from buffer item:6
After consuming buffer:
0	7	8	0	0	tejal@ubuntu:~/Desktop$ 


*/
