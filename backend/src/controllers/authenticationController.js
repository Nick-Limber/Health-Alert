import { db_pool } from "../config/db.js";
import argon2 from "argon2";
import { generateToken } from "../utils/generateToken.js";

const register = async (req, res) => {

    const { username, email, d_o_b, password } = req.body;

    try {
        const [rows] = await (db_pool.execute("SELECT username, email FROM profile WHERE username = ? OR email = ?", [username, email]));

        if (rows.length > 0) {

            const usernameTaken = rows.some(user => user.username === username);
            const emailTaken = rows.some(user => user.email === email);

            if (usernameTaken && emailTaken) {
                return res.status(400).json({ message: "username and email already taken" });
            }
            else if (usernameTaken) {
                return res.status(400).json({ message: "username already taken" });
            }
            else {
                return res.status(400).json({ message: "email already in use" })
            }
        }

        const hashedpassword = await argon2.hash(password);

        const [result] = await db_pool.execute("INSERT INTO profile (username, email, d_o_b, password) VALUES (?, ?, ?,?)", [username, email, d_o_b, hashedpassword]);

        const token = generateToken(result.insertID);


        res.status(201).json({
            status: "succcess",
            data: {
                user: {
                    id: result.insertID,
                    name: username,
                    email: email
                },
                token,
            },
        });

    } catch (error) {
        res.status(500).json({ message: `${error}` });
    }
}



const login = async (req, res) => {
    const { email, password } = req.body;

    try {
        const login_SQL = "SELECT profile_id, password FROM profile WHERE email = ? LIMIT 1";
        const [rows] = await db_pool.execute(login_SQL, [email]);

        if (!rows || rows.length === 0) {
            return res.status(401).json({ error: "invalid email or password" });
        }

        // Returns a boolean (True if the passwords match ---- False if the passwords don't)
        const validPassword = await argon2.verify(rows[0].password, password);

        if (!validPassword) {
            return res.status(401).json({ error: "invalid email or password" });
        }

        const token = generateToken(rows[0].userID);

        res.status(201).json({
            status: "succcess",
            data: {
                user: {
                    id: rows[0].userID,
                    email: email
                },
                token,
            },
        });


    }
    catch (error) {
        res.status(500).json({ error: `${error}` });
    }
}

export { register, login };