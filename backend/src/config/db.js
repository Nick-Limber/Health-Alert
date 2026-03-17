import mysql from "mysql2/promise";
import { config } from "dotenv";

config();

const db_pool = mysql.createPool({
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

const close_pool = async () => {
    console.log("database closing");
    await db_pool.end();
}

export { db_pool, close_pool };