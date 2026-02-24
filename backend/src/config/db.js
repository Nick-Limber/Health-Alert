import mysql from "mysql2/promise";
import { config } from "dotenv";

config();

const db_pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    waitForConnections: true,
    connectionLimit: 10
});

const close_pool = async () => {
    console.log("database closing");
    await pool.end();
}

export { db_pool, close_pool };