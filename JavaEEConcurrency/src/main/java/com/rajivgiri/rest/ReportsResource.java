package com.rajivgiri.rest;

import java.beans.PropertyVetoException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.rajivgiri.beans.BankAccount;
import com.rajivgiri.dao.BankAccountDAO;
import com.rajivgiri.runnables.ReportsProcessor;

@Path("reports")
public class ReportsResource {

	private BankAccountDAO dao;

	@Resource(lookup = "java:jboss/ee/concurrency/executor/default")

	private ManagedExecutorService service;

	public ReportsResource() {

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {

			dataSource.setJdbcUrl("jdbc:sqlserver://Server-Name;databaseName=Database-Name");
			dataSource.setDriverClass("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dataSource.setUser("Login-Name");
			dataSource.setPassword("Login-Password");
			dao = new BankAccountDAO(dataSource);

		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dataSource != null) {
			System.out.println("YAY we are Connected");
		}

	}

	@GET
	@Path("/")
	public String generateReports() {

		System.out.println("service object from JNDI look up : " + service);
		List<BankAccount> accounts = dao.getAllBankAccounts();

		for (BankAccount account : accounts) {

			try {
				Future<Boolean> future = service.submit(new ReportsProcessor(account, dao));

				System.out.println("Report Generated ?" + future.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return "Report generation tasks submitted!";
	}

	/*
	 * public ReportsResource() {
	 *
	 * //JNDI lookup
	 *
	 * InitialContext contex; try { contex = new InitialContext();
	 * ManagedExecutorService executorService=
	 * (ManagedExecutorService)contex.lookup(
	 * "java:jboss/ee/concurrency/executor/default");
	 *
	 * } catch (NamingException e) { // TODO Auto-generated catch block
	 * e.printStackTrace();
	 * System.err.println("Check for JNDI name resources at chrome bookmark"); }
	 *
	 * }
	 */

}
