from datetime import timedelta
from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from airflow.utils.dates import days_ago

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': days_ago(2),
    'email': ['wirtzleg@gmail.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5)
}

dag = DAG(
    'etf-analyze',
    default_args=default_args,
    description='Etf-analyze spark job',
    schedule_interval=None,
    start_date= days_ago(2)
)

t1 = BashOperator(
    task_id='run_spark_job',
    bash_command="""docker exec spark-master spark-submit /jobs/etf_job-assembly-0.0.1.jar""",
    dag=dag,
)

t2 = BashOperator(
    task_id='echo_finished',
    bash_command='echo finished',
    dag=dag,
)

t1 >> t2
