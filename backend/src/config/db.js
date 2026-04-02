import mysql from "mysql2/promise";
//makes csPath work
import path from "path";

//config();

//const caPath = path.resolve("./certs/ca.pem"); //path from backend folder
const db_pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    //added
    port:process.env.DB_PORT,
    waitForConnections: true,
    connectionLimit: 10,
    enableKeepAlive: true,        // Prevents the "Reset" error you saw
    keepAliveInitialDelay: 10000, // Sends a "ping" every 10 seconds
    ssl: {
        ca: fs.readFileSync(Path),
        rejectUnauthorized: true  // Ensures the cert is strictly validated
    }
});

const close_pool = async () => {
    console.log("database closing");
    await db_pool.end();
}

export { db_pool, close_pool };