import jwt from "jsonwebtoken";
import { db_pool } from "../config/db.js";


const verificationMiddleware = async (req, res, next) => {
    let token;

    if (req.headers.authorization && req.headers.authorization.startsWith("Bearer")) {
        token = req.headers.authorization.split(" ")[1];
    }

    if (!token) {
        return res.status(401).json({ error: "Not Authorized" });
    }

    try {
        // Decode JWT (Token gets decoded into payload -> id: userID)
        const decode = jwt.verify(token, process.env.TOKEN_SECRET);

        const [[user]] = await db_pool.execute("SELECT profile_id FROM profile WHERE profile_id = ?", [decode.id]);

        if (!user) {
            return res.status(401).json({ error: "No user" });
        }

        req.user = user.profile_id;
        next();

    } catch (error) {
        return res.status(401).json({ error: `${error}` });
    }
}
export { verificationMiddleware };