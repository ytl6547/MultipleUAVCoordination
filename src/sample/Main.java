package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

import static java.lang.Math.*;

class Robot{
    public Queue<Vec> seq=new LinkedList<>();

    public Robot(int[][] space, int currRow, int currCol){
        this.space = space;
        this.currRow = currRow;
        this.currCol = currCol;
    }

    public boolean move(){
        int newRow = currRow+currDir[0];
        int newCol = currCol+currDir[1];
        if(newRow>=0 && newRow<space.length && newCol>=0 && newCol<space[newRow].length  && space[newRow][newCol]!=0){
            currRow=newRow;
            currCol=newCol;
            Vec temp = new Vec(currCol*50+25, currRow*50 +25);
            seq.add(temp);
            return true;
        }
        else{
            return false;
        }
    }
    public void turnLeft(){
        if(currDir[0]==1 && currDir[1]==0){
            currDir[0]=0;
            currDir[1]=1;
        }
        else if(currDir[0]==0 && currDir[1]==1){
            currDir[0]=-1;
            currDir[1]=0;
        }
        else if(currDir[0]==-1&&currDir[1]==0){
            currDir[0]=0;
            currDir[1]=-1;
        }
        else if(currDir[0]==0&&currDir[1]==-1){
            currDir[0]=1;
            currDir[1]=0;
        }

    }
    public void turnRight(){
        if(currDir[0]==1 && currDir[1]==0){
            currDir[0]=0;
            currDir[1]=-1;
        }
        else if(currDir[0]==0 && currDir[1]==1){
            currDir[0]=1;
            currDir[1]=0;
        }
        else if(currDir[0]==-1 && currDir[1]==0){
            currDir[0]=0;
            currDir[1]=1;
        }
        else if(currDir[0]==0 && currDir[1]==-1){
            currDir[0]=-1;
            currDir[1]=0;
        }
    }
    public void detect(){
        space[currRow][currCol]=2;
        for(int i=0; i<space.length; i++){
            for(int j=0; j<space[i].length; j++){
                System.out.print(space[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    public int[][] space;
    public int currRow, currCol;
    public int[] currDir = {0,1};
//    public void setSpace(int[][] space, int cu){
//        this.space = space;
//    }

};

class Solver {

    public void detectSpace(Robot robot) {
        // A number can be added to each visited cell
        // use string to identify the class
        Set<String> set = new HashSet<>();
        int cur_dir = 0;   // 0: up, 90: right, 180: down, 270: left
        backtrack(robot, set, 0, 0, 0);
    }

    public void backtrack(Robot robot, Set<String> set, int i,
                          int j, int cur_dir) {
        String tmp = i + "->" + j;
        if(set.contains(tmp)) {
            return;
        }

        robot.detect();
        set.add(tmp);

        for(int n = 0; n < 4; n++) {
            // the robot can to four directions, we use right turn
            if(robot.move()) {
                // can go directly. Find the (x, y) for the next cell based on current direction
                int x = i, y = j;
                switch(cur_dir) {
                    case 0:
                        // go up, reduce i
                        x = i-1;
                        break;
                    case 90:
                        // go right
                        y = j+1;
                        break;
                    case 180:
                        // go down
                        x = i+1;
                        break;
                    case 270:
                        // go left
                        y = j-1;
                        break;
                    default:
                        break;
                }

                backtrack(robot, set, x, y, cur_dir);
                // go back to the starting position
                robot.turnLeft();
                robot.turnLeft();
                robot.move();
                robot.turnRight();
                robot.turnRight();
            }

            // turn to next direction
            robot.turnRight();
            cur_dir += 90;
            cur_dir %= 360;
        }

    }
}

public class Main extends Application {

    static Queue<Vec> goals;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        primaryStage.setTitle("Boids");

        Canvas canvas = new Canvas(800, 600);


        root.getChildren().add(canvas);
        Scene myScene = new Scene(root);
        Flock f = new Flock();
        f.addBoid(new Boid(40, 80));
        f.addBoid(new Boid(40, 40));
//        f.addBoid(new Boid(60, 60));
        f.addBoid(new Boid(80, 40));
        f.addBoid(new Boid(80, 80));
//        f.addBoid(new Boid(600, 500));
        myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println("mouse click detected! " + mouseEvent.getSource());
                System.out.println(mouseEvent.getX()+" "+mouseEvent.getY());
                f.setGoal(mouseEvent.getX(), mouseEvent.getY());
//                f.addBoid(new Boid(mouseEvent.getX(), mouseEvent.getY()));
            }
        });


        final Timeline timeline = new Timeline();
        timeline.setCycleCount(2);
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1)));
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                f.run(gc, 800, 600, goals);
            }

        };


        primaryStage.setScene(myScene);
        primaryStage.show();
        timer.start();
        timeline.play();
    }


    public static void main(String[] args) {
        Solver mySolver = new Solver();
        int row = 600/50;
        int col = 800/50;
        int[][] space = new int[row][col];
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                space[i][j]=1;
            }
        }

        Robot myRobot = new Robot(space,0, 0);
        mySolver.detectSpace(myRobot);
        goals = myRobot.seq;
        launch(args);
    }
}
//class Obstacle{
//    public Obstacle(){
//
//    }
//}
class Boid {
    static final Random r = new Random();
//    static final Vec migrate = new Vec(0.1, 0.1);
    static final int size = 3;

    static double maxSpeed, maxForce;

    public boolean leader;

    Vec location, velocity, acceleration;

    Vec goal = new Vec(100,  100);

    private boolean included = true;

    Boid(double x, double y) {
        acceleration = new Vec();
        velocity = new Vec(0, 0);
        location = new Vec(x, y);
        maxSpeed = 2.3;
        maxForce = 0.05;
    }

    void update() {
        velocity.add(acceleration);
        velocity.limit(maxSpeed);
        location.add(velocity);
        acceleration.mult(0);
    }

    void applyForce(Vec force) {
        acceleration.add(force);
    }

    Vec seek(Vec target) {
        Vec steer = Vec.sub(target, location);
        steer.normalize();
        steer.mult(maxSpeed);
        steer.sub(velocity);
        steer.limit(maxForce);
        return steer;
    }

    void flock(java.util.List<Boid> boids) {
        view(boids);

        Vec rule1 = separation(boids);
        Vec rule2 = alignment(boids);
        Vec rule3 = cohesion(boids);
//        System.out.println(rule1.x + " " + rule1.y);
//        System.out.println(rule2.x + " " + rule2.y);
//        System.out.println(rule3.x + " " + rule3.y);
//        System.out.println();
//        bounce(boids);

        rule1.mult(2.5);
        rule2.mult(0.5);
        rule3.mult(1.8);
//        bounce.mult(1.5);
//        System.out.println(rule1.x + " " + rule1.y);
//        System.out.println(rule2.x + " " + rule2.y);
//        System.out.println(rule3.x + " " + rule3.y);
//        System.out.println();

        applyForce(rule1);
        applyForce(rule2);
        applyForce(rule3);
//        applyForce(bounce);
//        applyForce(migrate);
    }

    void view(List<Boid> boids) {
        double sightDistance = 1200;
//        double peripheryAngle = PI * 0.85;

        for (Boid b : boids) {
            b.included = false;

            if (b == this)
                continue;

            double d = Vec.dist(location, b.location);
            if (d <= 0 || d > sightDistance)
                continue;

//            Vec lineOfSight = Vec.sub(b.location, location);
//
//            double angle = Vec.angleBetween(lineOfSight, velocity);
//            if (angle < peripheryAngle)
            b.included = true;
        }
    }
    Vec separation(java.util.List<Boid> boids) {
        double desiredSeparation = 45;

        Vec steer = new Vec(0, 0);
        int count = 0;
        for (int i=0; i<boids.size(); i++) {
            if (!boids.get(i).included)
                continue;

            double d = Vec.dist(location, boids.get(i).location);
            if ((d > 0) && (d < desiredSeparation)) {
                Vec diff = Vec.sub(location, boids.get(i).location);
                diff.normalize();
                diff.mult(1/d-1/desiredSeparation);
                diff.div(d*d);        // weight by distance
                steer.add(diff);
                count++;
//                if(boids.get(i).leader){
//                    steer.add(diff);
//                    count++;
//                }
            }
        }
        if (count > 0) {
            steer.div(count);
        }

        if (steer.mag() > 0) {
            steer.normalize();
            steer.mult(maxSpeed);
            steer.sub(velocity);
            steer.limit(maxForce);
            return steer;
        }
        return new Vec(0, 0);
    }

    Vec alignment(java.util.List<Boid> boids) {
        double preferredDist = 70;

        Vec steer = new Vec(0, 0);
        int count = 0;

        for (int i=0; i<boids.size(); i++) {
            if (!boids.get(i).included)
                continue;

            double d = Vec.dist(location, boids.get(i).location);
            if ((d >= 0) && (d < preferredDist)) {
                steer.add(boids.get(i).velocity);
                count++;
//                if(boids.get(i).leader){
//                    steer.add(boids.get(i).velocity);
//                    count++;
//                }

            }
        }

        if (count > 0) {
            steer.div(count);
            steer.normalize();
            steer.mult(maxSpeed);
            steer.sub(velocity);
            steer.limit(maxForce);
        }
        return steer;
    }

    Vec cohesion(java.util.List<Boid> boids) {
        double preferredDist = 70;

        Vec target = new Vec(0, 0);
        int count = 0;

        for (int i=0; i<boids.size(); i++) {
            if (!boids.get(i).included)
                continue;
//            System.out.println("include");

            double d = Vec.dist(location, boids.get(i).location);
            if (d > preferredDist) {
                target.add(boids.get(i).location);
                count++;
//                if(boids.get(i).leader){
//                    target.add(boids.get(i).location);
//                    count++;
//                }
            }
        }
        if (count > 0) {
            target.div(count);
            return seek(target);
        }
        return target;
    }
    void draw(GraphicsContext gc, int w, int h) {

        gc.fillOval(location.x, location.y, 15, 15);
    }

    void run(GraphicsContext gc, List<Boid> boids, int w, int h, Queue<Vec> goals) {
        if(leader){
            if(Vec.dist(location, goal)<=Boid.maxSpeed/2){
                velocity=new Vec(0, 0);
                goals.poll();
            }
            else{
                Vec steer = Vec.sub(goal, location);
                steer.normalize();
                steer.mult(Boid.maxSpeed/2);
                velocity=steer;
                location.add(velocity);
            }
//            acceleration.mult(0);
        }
        else{
//            System.out.println("here");
            flock(boids);
            update();
        }
        draw(gc, w, h);
    }

    void setGoal(double x, double y){
        goal.x = x;
        goal.y = y;
    }
}

class Flock {
    List<Boid> boids;
    Vec goal=null;


    void run(GraphicsContext gc, int w, int h, Queue<Vec> goals) {
        Vec newGoal = goals.peek();
        if(newGoal==null){
            return;
        }
        if(goal==null||goal.x!=newGoal.x||goal.y!=newGoal.y){
            goal = newGoal;
            System.out.println(goal.x+" "+goal.y);
        }
        gc.clearRect(0,0, w, h);
        double MIN = 10000000;
        int index = -1;
        for(int i=0; i<boids.size(); i++){
            double temp = Vec.dist(boids.get(i).location, goal);
            if(temp<MIN){
                MIN = temp;
                index = i;
            }
        }
        for(int i=0; i<boids.size(); i++){
            if(i==index){
                boids.get(i).leader=true;
            }
            else boids.get(i).leader=false;
        }

        for (int i=0; i<boids.size(); i++) {
            if(i==index){
                boids.get(i).setGoal(goal.x, goal.y);
            }
            boids.get(i).run(gc, boids, w, h, goals);
        }
    }

    Flock() {
        boids = new ArrayList<>();
    }

    void addBoid(Boid b) {
        boids.add(b);
    }
    void setGoal(double x, double y){
        goal.x = x;
        goal.y = y;
    }

}
class Vec {
    double x, y;

    Vec() {
    }

    Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    void add(Vec v) {
        x += v.x;
        y += v.y;
    }

    void sub(Vec v) {
        x -= v.x;
        y -= v.y;
    }

    void div(double val) {
        x /= val;
        y /= val;
    }

    void mult(double val) {
        x *= val;
        y *= val;
    }

    double mag() {
        return sqrt(pow(x, 2) + pow(y, 2));
    }

    double dot(Vec v) {
        return x * v.x + y * v.y;
    }

    void normalize() {
        double mag = mag();
        if (mag != 0) {
            x /= mag;
            y /= mag;
        }
    }

    void limit(double lim) {
        double mag = mag();
        if (mag != 0 && mag > lim) {
            x *= lim / mag;
            y *= lim / mag;
        }
    }

    double heading() {
        return atan2(y, x);
    }

    static Vec sub(Vec v, Vec v2) {
        return new Vec(v.x - v2.x, v.y - v2.y);
    }

    static double dist(Vec v, Vec v2) {
        return sqrt(pow(v.x - v2.x, 2) + pow(v.y - v2.y, 2));
    }

    static double angleBetween(Vec v, Vec v2) {
        return acos(v.dot(v2) / (v.mag() * v2.mag()));
    }
}