package com.learnmax.virtuallab.engine;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Real physics simulation engine using actual physics equations
 * Implements Newtonian mechanics for realistic simulations
 */
public class PhysicsEngine {
    
    // Physical constants
    public static final double GRAVITY = 9.81; // m/s²
    public static final double AIR_RESISTANCE = 0.1; // coefficient
    public static final double FRICTION = 0.3; // coefficient
    
    /**
     * Physical object with real properties
     */
    public static class PhysicsObject {
        private double mass; // kg
        private Point2D.Double position; // meters
        private Point2D.Double velocity; // m/s
        private Point2D.Double acceleration; // m/s²
        private Point2D.Double force; // Newtons
        private double angle; // radians
        private double angularVelocity; // rad/s
        private boolean fixed; // immovable object
        
        public PhysicsObject(double mass, double x, double y) {
            this.mass = mass;
            this.position = new Point2D.Double(x, y);
            this.velocity = new Point2D.Double(0, 0);
            this.acceleration = new Point2D.Double(0, 0);
            this.force = new Point2D.Double(0, 0);
            this.angle = 0;
            this.angularVelocity = 0;
            this.fixed = false;
        }
        
        public void applyForce(double fx, double fy) {
            if (!fixed) {
                force.x += fx;
                force.y += fy;
            }
        }
        
        public void update(double dt) {
            if (fixed) return;
            
            // F = ma, so a = F/m
            acceleration.x = force.x / mass;
            acceleration.y = force.y / mass;
            
            // v = v0 + at
            velocity.x += acceleration.x * dt;
            velocity.y += acceleration.y * dt;
            
            // Apply air resistance: F_drag = -k * v
            velocity.x *= (1 - AIR_RESISTANCE * dt);
            velocity.y *= (1 - AIR_RESISTANCE * dt);
            
            // x = x0 + vt
            position.x += velocity.x * dt;
            position.y += velocity.y * dt;
            
            // Update angle
            angle += angularVelocity * dt;
            
            // Reset force for next frame
            force.x = 0;
            force.y = 0;
        }
        
        // Getters and setters
        public double getMass() { return mass; }
        public Point2D.Double getPosition() { return position; }
        public Point2D.Double getVelocity() { return velocity; }
        public Point2D.Double getAcceleration() { return acceleration; }
        public double getAngle() { return angle; }
        public void setFixed(boolean fixed) { this.fixed = fixed; }
        public void setVelocity(double vx, double vy) {
            velocity.x = vx;
            velocity.y = vy;
        }
        public void setPosition(double x, double y) {
            position.x = x;
            position.y = y;
        }
        
        /**
         * Calculate kinetic energy: KE = 0.5 * m * v²
         */
        public double getKineticEnergy() {
            double speed = Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);
            return 0.5 * mass * speed * speed;
        }
        
        /**
         * Calculate potential energy: PE = m * g * h
         */
        public double getPotentialEnergy(double groundLevel) {
            return mass * GRAVITY * (position.y - groundLevel);
        }
        
        /**
         * Calculate momentum: p = m * v
         */
        public Point2D.Double getMomentum() {
            return new Point2D.Double(mass * velocity.x, mass * velocity.y);
        }
    }
    
    /**
     * Projectile motion calculator
     */
    public static class ProjectileMotion {
        private double initialVelocity;
        private double angle; // degrees
        private double height;
        
        public ProjectileMotion(double initialVelocity, double angle, double height) {
            this.initialVelocity = initialVelocity;
            this.angle = angle;
            this.height = height;
        }
        
        /**
         * Calculate maximum height
         */
        public double getMaxHeight() {
            double angleRad = Math.toRadians(angle);
            double vy = initialVelocity * Math.sin(angleRad);
            return height + (vy * vy) / (2 * GRAVITY);
        }
        
        /**
         * Calculate range (horizontal distance)
         */
        public double getRange() {
            double angleRad = Math.toRadians(angle);
            double vx = initialVelocity * Math.cos(angleRad);
            double vy = initialVelocity * Math.sin(angleRad);
            
            // Time to hit ground: h = h0 + vy*t - 0.5*g*t²
            // Solve quadratic equation
            double a = -0.5 * GRAVITY;
            double b = vy;
            double c = height;
            
            double discriminant = b * b - 4 * a * c;
            if (discriminant < 0) return 0;
            
            double t = (-b - Math.sqrt(discriminant)) / (2 * a);
            return vx * t;
        }
        
        /**
         * Calculate time of flight
         */
        public double getTimeOfFlight() {
            double angleRad = Math.toRadians(angle);
            double vy = initialVelocity * Math.sin(angleRad);
            
            double a = -0.5 * GRAVITY;
            double b = vy;
            double c = height;
            
            double discriminant = b * b - 4 * a * c;
            if (discriminant < 0) return 0;
            
            return (-b - Math.sqrt(discriminant)) / (2 * a);
        }
        
        /**
         * Get position at time t
         */
        public Point2D.Double getPositionAtTime(double t) {
            double angleRad = Math.toRadians(angle);
            double vx = initialVelocity * Math.cos(angleRad);
            double vy = initialVelocity * Math.sin(angleRad);
            
            double x = vx * t;
            double y = height + vy * t - 0.5 * GRAVITY * t * t;
            
            return new Point2D.Double(x, y);
        }
        
        /**
         * Get trajectory points for visualization
         */
        public List<Point2D.Double> getTrajectory(int numPoints) {
            List<Point2D.Double> points = new ArrayList<>();
            double timeOfFlight = getTimeOfFlight();
            double dt = timeOfFlight / numPoints;
            
            for (int i = 0; i <= numPoints; i++) {
                double t = i * dt;
                points.add(getPositionAtTime(t));
            }
            
            return points;
        }
    }
    
    /**
     * Collision detection and response
     */
    public static class CollisionHandler {
        
        /**
         * Check collision between two circles
         */
        public static boolean checkCircleCollision(PhysicsObject obj1, double r1,
                                                   PhysicsObject obj2, double r2) {
            double dx = obj2.position.x - obj1.position.x;
            double dy = obj2.position.y - obj1.position.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            return distance < (r1 + r2);
        }
        
        /**
         * Resolve elastic collision between two objects
         * Conservation of momentum and energy
         */
        public static void resolveCollision(PhysicsObject obj1, PhysicsObject obj2) {
            // Calculate relative velocity
            double dvx = obj2.velocity.x - obj1.velocity.x;
            double dvy = obj2.velocity.y - obj1.velocity.y;
            
            // Calculate relative position
            double dx = obj2.position.x - obj1.position.x;
            double dy = obj2.position.y - obj1.position.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance == 0) return; // Avoid division by zero
            
            // Normal vector
            double nx = dx / distance;
            double ny = dy / distance;
            
            // Relative velocity in normal direction
            double dvn = dvx * nx + dvy * ny;
            
            // Don't resolve if objects are separating
            if (dvn > 0) return;
            
            // Calculate impulse scalar (elastic collision, e = 1)
            double impulse = 2 * dvn / (1/obj1.mass + 1/obj2.mass);
            
            // Apply impulse
            if (!obj1.fixed) {
                obj1.velocity.x += impulse * nx / obj1.mass;
                obj1.velocity.y += impulse * ny / obj1.mass;
            }
            
            if (!obj2.fixed) {
                obj2.velocity.x -= impulse * nx / obj2.mass;
                obj2.velocity.y -= impulse * ny / obj2.mass;
            }
            
            // Separate objects to prevent overlap
            double overlap = (distance - 0) / 2;
            if (!obj1.fixed) {
                obj1.position.x -= overlap * nx;
                obj1.position.y -= overlap * ny;
            }
            if (!obj2.fixed) {
                obj2.position.x += overlap * nx;
                obj2.position.y += overlap * ny;
            }
        }
    }
    
    /**
     * Spring physics (Hooke's Law: F = -kx)
     */
    public static class Spring {
        private PhysicsObject obj1;
        private PhysicsObject obj2;
        private double restLength;
        private double springConstant; // k
        private double damping; // damping coefficient
        
        public Spring(PhysicsObject obj1, PhysicsObject obj2, double restLength, double k) {
            this.obj1 = obj1;
            this.obj2 = obj2;
            this.restLength = restLength;
            this.springConstant = k;
            this.damping = 0.1;
        }
        
        public void update() {
            // Calculate distance
            double dx = obj2.position.x - obj1.position.x;
            double dy = obj2.position.y - obj1.position.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance == 0) return;
            
            // Calculate spring force: F = -k * (x - x0)
            double extension = distance - restLength;
            double forceMagnitude = -springConstant * extension;
            
            // Add damping: F_damping = -c * v
            double dvx = obj2.velocity.x - obj1.velocity.x;
            double dvy = obj2.velocity.y - obj1.velocity.y;
            double dampingForce = damping * (dvx * dx + dvy * dy) / distance;
            forceMagnitude -= dampingForce;
            
            // Apply force in direction of spring
            double fx = forceMagnitude * dx / distance;
            double fy = forceMagnitude * dy / distance;
            
            obj1.applyForce(-fx, -fy);
            obj2.applyForce(fx, fy);
        }
    }
    
    /**
     * Pendulum physics
     */
    public static class Pendulum {
        private double length;
        private double angle; // radians
        private double angularVelocity;
        private double angularAcceleration;
        private double damping;
        
        public Pendulum(double length, double initialAngle) {
            this.length = length;
            this.angle = Math.toRadians(initialAngle);
            this.angularVelocity = 0;
            this.angularAcceleration = 0;
            this.damping = 0.995; // slight damping
        }
        
        public void update(double dt) {
            // Angular acceleration: α = -(g/L) * sin(θ)
            angularAcceleration = -(GRAVITY / length) * Math.sin(angle);
            
            // Update angular velocity with damping
            angularVelocity += angularAcceleration * dt;
            angularVelocity *= damping;
            
            // Update angle
            angle += angularVelocity * dt;
        }
        
        public Point2D.Double getBobPosition(Point2D.Double pivot) {
            double x = pivot.x + length * Math.sin(angle);
            double y = pivot.y + length * Math.cos(angle);
            return new Point2D.Double(x, y);
        }
        
        public double getAngleDegrees() {
            return Math.toDegrees(angle);
        }
        
        public double getPeriod() {
            // T = 2π * sqrt(L/g)
            return 2 * Math.PI * Math.sqrt(length / GRAVITY);
        }
    }
    
    /**
     * Inclined plane physics
     */
    public static class InclinedPlane {
        private double angle; // degrees
        private double mass;
        private double friction;
        
        public InclinedPlane(double angle, double mass, double friction) {
            this.angle = angle;
            this.mass = mass;
            this.friction = friction;
        }
        
        /**
         * Calculate acceleration down the plane
         */
        public double getAcceleration() {
            double angleRad = Math.toRadians(angle);
            double parallelForce = mass * GRAVITY * Math.sin(angleRad);
            double normalForce = mass * GRAVITY * Math.cos(angleRad);
            double frictionForce = friction * normalForce;
            
            double netForce = parallelForce - frictionForce;
            return netForce / mass;
        }
        
        /**
         * Calculate normal force
         */
        public double getNormalForce() {
            return mass * GRAVITY * Math.cos(Math.toRadians(angle));
        }
        
        /**
         * Calculate friction force
         */
        public double getFrictionForce() {
            return friction * getNormalForce();
        }
    }
}
