package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Arco;
import it.polito.tdp.extflightdelays.model.Flight;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public void loadAllAirports(Map<Integer,Airport> idMap) {
		String sql = "SELECT * FROM airports";
		//List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!idMap.containsKey(rs.getInt("ID"))) {
					Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
							rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
							rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
					idMap.put(airport.getId(), airport);
				}
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				idMap.put(airport.getId(), airport);
				//result.add(airport);
			}

			conn.close();
			//return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Arco> getAllEdges(int distanza){
		String sql = "SELECT COUNT(*) AS frequenza,f1.ORIGIN_AIRPORT_ID, f1.DESTINATION_AIRPORT_ID, AVG(f1.DISTANCE) AS distanza"
				+ " FROM flights f1"
				+ " GROUP BY f1.ORIGIN_AIRPORT_ID, f1.DESTINATION_AIRPORT_ID";
		List<Flight> parziale = new ArrayList<Flight>();
		List<Arco> result = new ArrayList<Arco>();
		boolean trovato = false;
		
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				if(rs.getInt("distanza") > distanza)
				{
					trovato = false;
					Flight volo = new Flight(rs.getInt("ORIGIN_AIRPORT_ID"), rs.getInt("DESTINATION_AIRPORT_ID"),rs.getInt("distanza"),rs.getInt("frequenza"));
					for(int i=0;i<parziale.size();i++) {
						if(parziale.get(i).getOriginAirportId()==volo.getDestinationAirportId()
							&& parziale.get(i).getDestinationAirportId()==volo.getOriginAirportId()){
							int peso = (parziale.get(i).getFrequenze()*parziale.get(i).getDistance()+volo.getFrequenze()*volo.getDistance())/
									(parziale.get(i).getFrequenze()+volo.getFrequenze());
							parziale.get(i).setDistance(peso);
							parziale.get(i).setFrequenze(parziale.get(i).getFrequenze()+volo.getFrequenze());
							trovato = true;
						}
					}
					if(trovato==false) {
						parziale.add(volo);
					}
				}
			}
			
			for(int i=0;i<parziale.size();i++) {
				result.add(new Arco(parziale.get(i).getOriginAirportId(),parziale.get(i).getDestinationAirportId(),parziale.get(i).getDistance()));
			}
			conn.close();
			return result;
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
}
