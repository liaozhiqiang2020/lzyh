package com.wl.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NumberCruncher extends Remote {
	
	int[] factor(int number) throws RemoteException;

}
