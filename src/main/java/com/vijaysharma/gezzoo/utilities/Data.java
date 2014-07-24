package com.vijaysharma.gezzoo.utilities;

import java.util.Arrays;

import com.vijaysharma.gezzoo.models.Board;
import com.vijaysharma.gezzoo.models.Character;

public class Data {

	public static Board createBoard() {
		Board board = new Board(1L);
		board.setName("Bollywood");
		board.setCharacters(Arrays.asList(
			Character.newCharacter("Shah Rukh Khan", "bollywood", "/images/bollywood/MV5BMTQxMjg4Mzk1Nl5BMl5BanBnXkFtZTcwMzQyMTUxNw@@._V1_SY317_CR1,0,214,317_.jpg"),
			Character.newCharacter("Arjun Rampal", "bollywood", "/images/bollywood/MV5BMTg0NTAzOTU4Ml5BMl5BanBnXkFtZTcwMzg1MjUyOA@@._V1_SX214_CR0,0,214,317_.jpg"),
			Character.newCharacter("Anupam Kher", "bollywood", "/images/bollywood/MV5BMTY0MDkxMzkwN15BMl5BanBnXkFtZTcwODUxNTA5Nw@@._V1_SX214_CR0,0,214,317_.jpg"),
			Character.newCharacter("Sanjay Dutt", "bollywood", "/images/bollywood/MV5BNzU2NTgwNzY1OF5BMl5BanBnXkFtZTcwMjQxNzcxOA@@._V1_SY317_CR131,0,214,317_.jpg"),
			Character.newCharacter("Hrithik Roshan", "bollywood", "/images/bollywood/MV5BMTU1MjQzNDExN15BMl5BanBnXkFtZTcwNzIxMTg0Mw@@._V1_SY317_CR173,0,214,317_.jpg"),
			Character.newCharacter("Akshay Kumar", "bollywood", "/images/bollywood/MV5BMTU4NzM4Nzk1OF5BMl5BanBnXkFtZTcwMTAwMTA1NA@@._V1_SY317_CR104,0,214,317_.jpg"),
			Character.newCharacter("Salman Khan", "bollywood", "/images/bollywood/MV5BMTIyODQyOTA1M15BMl5BanBnXkFtZTcwMDQxNDIzMQ@@._V1_SY317_CR56,0,214,317_.jpg"),
			Character.newCharacter("Ranveer Singh", "bollywood", "/images/bollywood/MV5BMTExOTcwMDU4NTReQTJeQWpwZ15BbWU3MDA0MjE1MTc@._V1_SY317_CR1,0,214,317_.jpg"),
			Character.newCharacter("Emraan Hashmi", "bollywood", "/images/bollywood/MV5BMjExNzExNDY3OV5BMl5BanBnXkFtZTcwNzAyMTg2OA@@._V1_SY317_CR0,0,214,317_.jpg"),
			Character.newCharacter("Amitabh Bachchan", "bollywood", "/images/bollywood/MV5BNTk1OTUxMzIzMV5BMl5BanBnXkFtZTcwMzMxMjI0Nw@@._V1_SY317_CR8,0,214,317_.jpg"),
			Character.newCharacter("Abhishek Bachchan", "bollywood", "/images/bollywood/MV5BMTk1NjU1NjkxN15BMl5BanBnXkFtZTcwNDI4MDMzMg@@._V1_SY317_CR10,0,214,317_.jpg"),
			Character.newCharacter("John Abraham", "bollywood", "/images/bollywood/MV5BMTY1MDUyMjI1N15BMl5BanBnXkFtZTYwMjg4MjA0._V1_SX214_CR0,0,214,317_.jpg"),
			Character.newCharacter("Deepika Padukone", "bollywood", "/images/bollywood/MV5BMjAxMTAwMTY2MV5BMl5BanBnXkFtZTcwOTM5NTQyMg@@._V1_SX214_CR0,0,214,317_.jpg"),
			Character.newCharacter("Katrina Kaif", "bollywood", "/images/bollywood/MV5BMTUwODY3NzA3NF5BMl5BanBnXkFtZTcwNTEzNDg3OA@@._V1_SY317_CR6,0,214,317_.jpg'}"),
			Character.newCharacter("Anushka Sharma", "bollywood", "/images/bollywood/MV5BMTc0NDMzMDYyM15BMl5BanBnXkFtZTcwMDEwMTA1NA@@._V1_SY317_CR104,0,214,317_.jpg"),
			Character.newCharacter("Priyanka Chopra", "bollywood", "/images/bollywood/MV5BMjAxNzUwNjExOV5BMl5BanBnXkFtZTcwNDUyMTUxNw@@._V1_SY317_CR105,0,214,317_.jpg"),
			Character.newCharacter("Aishwarya Rai", "bollywood", "/images/bollywood/MV5BMjEyMjEyODkzN15BMl5BanBnXkFtZTcwODkxMTY1Mg@@._V1_SX214_CR0,0,214,317_.jpg"),
			Character.newCharacter("Sonakshi Sinha", "bollywood", "/images/bollywood/MV5BOTg3MzQxMTkwOF5BMl5BanBnXkFtZTcwNjcyOTM5NA@@._V1_SY317_CR18,0,214,317_.jpg"),
			Character.newCharacter("Vidya Balan", "bollywood", "/images/bollywood/MV5BNDI3Mjk2MjgzMl5BMl5BanBnXkFtZTcwODQwMjI1OQ@@._V1_SY317_CR3,0,214,317_.jpg"),
			Character.newCharacter("Rani Mukerji", "bollywood", "/images/bollywood/MV5BMTg0MTI5MDkyMV5BMl5BanBnXkFtZTcwNzIyMDQ4Mg@@._V1_SY317_CR111,0,214,317_.jpg"),
			Character.newCharacter("Preity Zinta", "bollywood", "/images/bollywood/MV5BMTQyMDc4NTE1Ml5BMl5BanBnXkFtZTcwOTQwMDgxOA@@._V1_SX214_CR0,0,214,317_.jpg"),
			Character.newCharacter("Kajol", "bollywood", "/images/bollywood/MV5BNzIyNDI1MTYwMV5BMl5BanBnXkFtZTcwNzg5MzcxMw@@._V1_SY317_CR131,0,214,317_.jpg"),
			Character.newCharacter("Madhuri Dixit", "bollywood", "/images/bollywood/MV5BMjI1MTMxMDMxMV5BMl5BanBnXkFtZTcwMTUzNzY3Nw@@._V1_SY317_CR18,0,214,317_.jpg"),
			Character.newCharacter("Juhi Chawla", "bollywood", "/images/bollywood/MV5BNzI1MzUxODczNV5BMl5BanBnXkFtZTcwNDUyNTA0MQ@@._V1_SY317_CR12,0,214,317_.jp")
        ));
		
		return board;
	}
	
}
