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
    // Added path to CA certificate file (PEM)
    ssl: {
            //ca: fs.readFileSync(caPath),
            rejectUnauthorized: false
        }
});

const close_pool = async () => {
    console.log("database closing");
    await db_pool.end();
}

export { db_pool, close_pool };