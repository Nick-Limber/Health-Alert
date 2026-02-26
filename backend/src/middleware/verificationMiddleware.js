import jwt from JsonWebToken;

export const verificationMiddleware = async (req, res, next) => {
    let token;

    if (req.headers.authorization && headers.authorization.startsWith("Bearer")) {
        token = req.headers.authroization.split(" ")[1];
    }

    if (!token) {
        return res.status(401).json({ error: "Not Authorized" });
    }

    try {
        // Decode JWT (Token gets decoded into payload -> id: userID)
        const decode = jwt.verify(token, process.env.TOKEN_SECRET);

        const [[user]] = db_pool.execute("SELECT userID FROM user WHERE userID == ?", [decode.id]);

        if (!user) {
            return res.ststus(401).json({ error: "No user" });
        }

        req.user = user;
        next();

    } catch (error) {
        return res.status(401).json({ error: `${error}` });
    }
}